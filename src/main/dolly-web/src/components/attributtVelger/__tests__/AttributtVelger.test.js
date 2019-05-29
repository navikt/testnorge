import React from 'react'
import { shallow } from 'enzyme'
import AttributtVelger from '../AttributtVelger'
import { Radio } from 'nav-frontend-skjema'

describe('AttributtVelger.js', () => {
	const selectedIds = ['kjonn']
	const currentBestilling = {
		identOpprettesFra: 'nyIdent',
		attributeIds: ['spraak', 'nasjonalitet']
	}
	const onToggle = jest.fn()
	const rendered = shallow(
		<AttributtVelger
			currentBestilling={currentBestilling}
			selectedIds={selectedIds}
			onToggle={onToggle}
		/>
	)
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

	it('should has search function', () => {
		const testdata = '1'
		const searchedOn1 = rendered.instance()._searchOnChange({ target: { value: testdata } })
		expect(rendered.state().search).toEqual(testdata)
	})
})
