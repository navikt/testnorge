import React from 'react'
import { shallow } from 'enzyme'
import AttributtVelger from '../AttributtVelger'

describe('AttributtVelger.js', () => {
	const selectedIds = ['kjonn']
	const onToggle = jest.fn()
	const rendered = shallow(<AttributtVelger selectedIds={selectedIds} onToggle={onToggle} />)
	it('should render', () => {
		expect(rendered.find('.attributt-velger_panels').exists()).toBe(true)
	})
	it('should render with kjonn selected', () => {
		expect(rendered.find('#kjonn').props().checked).toBe(true)
	})

	it('should render "partner" as a attribute', () => {
		expect(rendered.find('#partner').exists()).toBe(true)
	})

	it('should not render attribute that have parent', () => {
		expect(rendered.find('#partner_kjonn').exists()).toBe(false)
	})

	it('should fire onToggle', () => {
		const value = 'kjonn'
		rendered.find('#kjonn').simulate('change', { target: { id: value } })
		expect(onToggle).toBeCalledWith(value)
	})

	it('should set searchfield to partner and only find that attribute', () => {
		rendered.setState({ search: 'partner' })
		expect(rendered.find('#kjonn').exists()).toBe(false)
		expect(rendered.find('#partner').exists()).toBe(true)
	})

	it('should set searchfield and render no items', () => {
		rendered.setState({ search: 'doesNotExist' })
		expect(
			rendered
				.find('.attributt-velger_panels')
				.find('p')
				.text()
		).toBe('Søket ga ingen treff')
	})
})
