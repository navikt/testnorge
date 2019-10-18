import React from 'react'
import { shallow } from 'enzyme'
import Utvalg from '../Utvalg'

describe('Utvalg.js', () => {
	it('should render without any ids selected', () => {
		const selectedIds = []
		const rendered = shallow(<Utvalg selectedIds={selectedIds} />)
		expect(rendered.find('.utvalg').exists()).toBe(true)
		expect(rendered.find('.utvalg--empty-result').text()).toBe('Ingenting er valgt')
	})

	it('should render ids that are selected', () => {
		const selectedIds = ['kjonn']
		const rendered = shallow(<Utvalg selectedIds={selectedIds} />)
		expect(rendered.find('ul').exists()).toBe(true)
	})
})
