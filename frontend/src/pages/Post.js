import axios from 'axios';
import React from 'react';

const Post = ({ws}) => {
  const [email, setEmail] = React.useState('');
  const [info, setInfo] = React.useState('');
  const [myType, setMyType] = React.useState('hidden');
  const [inquiryList, setInquiryList] = React.useState([]);
  const [dataList, setDataList] = React.useState([]);

  const handleEmailUpdate = (e) => {
    setEmail(e.target.value);
  };

  const handleInfo = (e) => {
    setInfo(e.target.value);
  }

  const handleEmailSubmit = (e) => {
    if (email === "") {
      setMyType('hidden');
    } else {
      setMyType('visible');

      axios.get('/get-inquiries?email='+email) 
      .then((res) => {
        console.log(res.data);
        setInquiryList(res.data); 
      }).catch(console.log);

      axios.get('/get-by-email?email='+email) 
      .then((res) => {
        console.log(res.data);
        setDataList(res.data); 
      }).catch(console.log);
    }
    console.log(email);
    console.log(info);
  }

  const handlePost = () => {
    const body = {
      email: email,
      info: info
    };
    axios.post('/submit-post', body)
      .then((res) => console.log(res)
        
      )
      .catch(console.log);
      setEmail("");
      setInfo("");
  }

  React.useEffect(() => {

    ws.addEventListener('message', (message) => {
      // The data the server sends back
      const parsedData = JSON.parse(message.data);
      console.log(parsedData);
    });
  }, []);

  const handleDelete = (e) => {
    axios.post('/delete?email='+e);
  };

  return (
    <div id="container">
      <div class="createPost">
        <h1>Post</h1>
        <label>Email: </label>
        <input type="email" value={email} onChange={handleEmailUpdate} />
        <button onClick={handleEmailSubmit}> Submit Email</button>
        <div>
        </div>
        <label style={{ visibility: myType }}>Description</label>
        <input style={{ visibility: myType }} value={info} onChange={handleInfo} />
        <div>
        </div>
        <button style={{ visibility: myType }} onClick={handlePost}> Post </button>
      </div>

      <div class="inquiries">
        <h3>Inquiries</h3>
        {inquiryList.map((obj, i) =>
        (
          <div key={i}>
            <div>
              Email: {obj.email}
            </div>
            <div>
              Inquiry: {obj.inquiry}
            </div>
          </div>
        ))}
      </div>

      <div class="myPosts">
        
        {dataList.map((object, i) => 
          (<div key={i}>
            <div>
              Email: {object.email}
              <button onClick={() => handleDelete(object.email)}>Delete</button>
            </div>
            <div>
              Info: {object.info}
            </div>
            <div>
            </div>
            <br/>
          </div>)
      )}
      </div>
    </div >
  );
};

export default Post;