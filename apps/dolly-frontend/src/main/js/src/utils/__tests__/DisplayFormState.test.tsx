import { shallow } from 'enzyme'
import DisplayFormState from '../DisplayFormState'

describe('DisplayFormState.js', () => {
	const wrapper = shallow(<DisplayFormState />)

	it('should render pre', () => {
		expect(wrapper.find('pre').exists()).toBeTruthy()
	})

	it('it should render strong', () => {
		expect(wrapper.find('strong').text()).toBe('props')
	})
})
