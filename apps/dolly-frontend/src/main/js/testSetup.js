const Enzyme = require('enzyme')
const EnzymeAdapter = require('@wojtekmaj/enzyme-adapter-react-17')

// Setup enzyme's react adapter
Enzyme.configure({ adapter: new EnzymeAdapter() })
