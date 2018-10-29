import React from 'react'
import { shallow } from 'enzyme'
import PersonInfoBlock from '../PersonInfoBlock'

describe('PersonInfoBlock.js', () => {
	describe('rendering single infonlock', () => {
		const data = [
			{ id: 'test', label: 'test', value: 'testdata' },
			{ id: 'test2', label: 'test2', value: 'testdata2' }
		]
		const wrapper = shallow(<PersonInfoBlock data={data} />)
		it('should render infoblock', () => {
			expect(wrapper.find('.person-info-block').exists()).toBeTruthy()
		})
		it('should render two values in infoblock', () => {
			expect(wrapper.find('.person-info-block_content').children()).toHaveLength(data.length)
		})
	})
	describe('rendering infoblock with infoblock with multiple blocks', () => {
		const data = [
			{
				parent: 'pappa',
				id: 'pappa',
				label: 'pappa',
				value: [
					{ id: 'barn1', label: 'barn1', value: 'barn1' },
					{ id: 'barn2', label: 'barn2', value: 'barn2' }
				]
			},
			{
				parent: 'pappa',
				id: 'pappa',
				label: 'pappa',
				value: [
					{ id: 'barn3', label: 'barn3', value: 'barn3' },
					{ id: 'barn4', label: 'barn4', value: 'barn4' }
				]
			}
		]
		const wrapper = shallow(<PersonInfoBlock data={data} multiple />)
		it('should render multiple infoblocks', () => {
			expect(wrapper.find('.person-info-block')).toHaveLength(data.length)
		})
	})
})
