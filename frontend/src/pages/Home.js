import axios from 'axios';
import React from 'react';

const Home = ({ ws } ) => {
  const [inquiry, setInquiry] = React.useState('');
  const [email, setEmail] = React.useState('');
  const [dataList,setDataList] = React.useState([]);

  const handleInquiry = (e) => {
    setInquiry(e.target.value);
  }

  const handleSubmit = (email) => {
    const body = {
      email: email,
      inquiry: inquiry,
    };

    console.log(inquiry);

    axios.post('/submit-inquiry', body)
    .then(res => {
      console.log(res);
      console.log(body)
      }
    )
  };

  const fetchData = () => {
    axios.get('/get-posts') // asyc
      .then((res) => {
        // res is what the spark server sent back
        console.log(res.data);
        setDataList(res.data); // save for using on the page
      }).catch(console.log);
  };

  React.useEffect(() => {
    // Trigger only 1 time
    fetchData();

    ws.addEventListener('message', (message) => {
      //when server sends data
      const parsedData = JSON.parse(message.data);
      console.log(parsedData);
      fetchData();
    });
  }, []); 

  return (
    <div>
      <h1>Home</h1>
      <div>
        {dataList.map((object, i) => 
          (<div key={i}>
            <div>
              Email: {object.email}
              {() => setEmail(object.email)}
            </div>
            <div>
              Info: {object.info}
            </div>
              <input id={i} class="textbox"type="text" value={inquiry} onChange={handleInquiry} name="inquiry"></input>
              <br/>
              <button onClick={() => handleSubmit(object.email)}>Submit Inquiry</button>
            <div>
            </div>
            <br/>
          </div>)
      )}
      </div>
    </div>
  );
};

export default Home;