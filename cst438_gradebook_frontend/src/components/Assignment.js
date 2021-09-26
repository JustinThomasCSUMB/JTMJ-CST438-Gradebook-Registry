import React, { Component } from 'react';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { Link } from 'react-router-dom'
import Cookies from 'js-cookie';
import Button from '@material-ui/core/Button';
import Radio from '@material-ui/core/Radio';
import {DataGrid} from '@material-ui/data-grid';
import {SERVER_URL} from '../constants.js'

// NOTE:  for OAuth security, http request must have
//   credentials: 'include' 
//

class Assignment extends Component {
    constructor(props) {
      super(props);
      this.state = {selected: 0, rows: [], name: "", dueDate: "", course: ""};
    };
 
   componentDidMount() {
    this.fetchAssignments();
    // TODO: fetch courses for UI store in props
  }
 
  fetchAssignments = () => {
    console.log("Assignment.fetchAssignments");
    const token = Cookies.get('XSRF-TOKEN');
    fetch(`${SERVER_URL}/gradebook`, 
      {  
        method: 'GET', 
        headers: { 'X-XSRF-TOKEN': token }
      } )
    .then((response) => response.json()) 
    .then((responseData) => { 
      if (Array.isArray(responseData.assignments)) {
        //  add to each row attribute "id"  This is required by DataGrid  id is the index value of row in table 
        this.setState({ rows: responseData.assignments.map((row, index) => ( { id: index, ...row } )) });
      } else {
        toast.error("Fetch failed.", {
          position: toast.POSITION.BOTTOM_LEFT
        });
      }        
    })
    .catch(err => console.error(err)); 
  }// fetchAssignments
  
  onRadioClick = (event) => {
    console.log("Assignment.onRadioClick " + event.target.value);
    this.setState({selected: event.target.value});
  }// onRadioClick

  onNameChange = (e) => {
    this.setState({name: e.target.value});
  }

  onDateChange = (e) => {
    this.setState({date: e.target.value});
  }

  onCourseChange = (e) => {
    this.setState({course: e.target.value});
  }

  addAssignment = () => {
    console.log("adding assignment");
    const token = Cookies.get('XSRF-TOKEN');

    fetch(`${SERVER_URL}/gradebook/addAssignment`,
    {
      method: 'POST',
      headers: { 'X-XSRF-TOKEN': token ,'Content-Type': 'application/json'},
      body: JSON.stringify({
        assignmentName: this.state.name,
        dueDate: this.state.dueDate,
        courseId: this.state.course
      })
    }
    ).then(res => {
          if (res.ok) {
            toast.success("Assignment Added Successfully", {
            position: toast.POSITION.BOTTOM_LEFT
            });
            this.fetchAssignments();
          } else {
            toast.error("Add Assignment failed", {
            position: toast.POSITION.BOTTOM_LEFT
            });
            console.error('POST http status =' + res.status);
      }})
        .catch(err => {
          toast.error("Add Assignment failed", {
            position: toast.POSITION.BOTTOM_LEFT
          });
          console.error(err);
        });    
  }// addAssignment
  
  render() {
     const columns = [
      {
        field: 'assignmentName',
        headerName: 'Assignment',
        width: 400,
        renderCell: (params) => (
          <div>
          <Radio
            checked={params.row.id == this.state.selected}
            onChange={this.onRadioClick}
            value={params.row.id}
            color="default"
            size="small"
          />
          {params.value}
          </div>
        )
      },
      { field: 'courseTitle', headerName: 'Course', width: 300 },
      { field: 'dueDate', headerName: 'Due Date', width: 200 }
      ];
      return (
        <div id="assignmentBody">
          <div align="left" >
                <h4>Assignment(s) ready to grade: </h4>
                  <div style={{ height: 450, width: '100%', align:"left"   }}>
                    <DataGrid rows={this.state.rows} columns={columns} />
                  </div>                
                <Button component={Link} to={{pathname:'/gradebook' , assignment: this.state.rows[this.state.selected]}} 
                        variant="outlined" color="primary" disabled={this.state.rows.length==0}  style={{margin: 10}}>
                  Grade
                </Button>
          </div>

          <div> 
            <h4>Add New Assignment To Gradebook</h4>
            <label for="assignmentName">Assignment Name: </label>
            <input type="text" id="assignmentName" name="assignmentName" onChange={this.onNameChange}></input><br></br>
            {/*TODO: date time picker*/}
            <label for="assignmentDueDate">Assignment Due Date: </label>
            <input type="text" id="assignmentDueDate" name="assignmentDueDate" placeholder="09/21/21 11:59pm" onChange={this.onDateChange}></input><br></br>
            {/*TODO: dropdown list of course names*/}
            <label for="courseName">Course Name: </label>
            <input type="text" id="courseName" onChange={this.onCourseChange}></input><br></br>
            <Button onClick={this.addAssignment} to={{pathname:'/gradebook/addAssignment'}}>
              Add Assignment
            </Button>
          </div>        

        </div>
      )
  }
}  

export default Assignment;