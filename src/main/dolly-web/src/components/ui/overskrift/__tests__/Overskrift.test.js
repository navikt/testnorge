import React from 'react'
import { shallow } from 'enzyme'
import Overskrift from '../Overskrift'

describe('Overskrift.js', () => {
	it('should render with defaults props', () => {
		const wrapper = shallow(<Overskrift />)
		// Default type
		expect(wrapper.type()).toBe('h1')
		expect(wrapper.hasClass('overskrift')).toBe(true)
		expect(wrapper.text()).toBe('default label')
	})

	it('should render the type provided', () => {
		const wrapper = shallow(<Overskrift type="h3" />)
		expect(wrapper.type()).toBe('h3')
	})

	it('should render label prop', () => {
		const testLabel = 'TestOverskrift'
		const wrapper = shallow(<Overskrift label={testLabel} />)
		expect(wrapper.text()).toBe(testLabel)
	})

	it('should render icons when actions prop is provided', () => {
		const actions = [
			{
				icon: 'iconlabel',
				onClick: jest.fn()
			}
		]
		const wrapper = shallow(<Overskrift actions={actions} />)
		expect(wrapper.find('Button')).toHaveLength(1)
		expect(wrapper.find('Button').props().onClick).toBe(actions[0].onClick)
		expect(wrapper.find('Button').props().kind).toBe(actions[0].icon)
	})

	it('should merge className prop', () => {
		const testClassName = 'TestClassName'
		const wrapper = shallow(<Overskrift className={testClassName} />)
		expect(wrapper.hasClass('overskrift', testClassName)).toBe(true)
	})
})
