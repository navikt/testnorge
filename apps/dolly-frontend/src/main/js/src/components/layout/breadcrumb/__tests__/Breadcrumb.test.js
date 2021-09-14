import Breadcrumb from '../Breadcrumb'
import React from 'react'
import { shallow } from 'enzyme'

const breadcrumbs = [
	{ key: '/', props: { match: { path: '/', url: '/' }, location: { pathname: '/yolo' } } },
	{
		key: '/yolo',
		props: { match: { path: '/yolo', url: '/yolo' }, location: { pathname: '/yolo' } },
	},
]

describe('Breadcrumb.js', () => {
	it('should render', () => {
		const rendered = shallow(<Breadcrumb breadcrumbs={breadcrumbs} />)
		expect(rendered.find('.breadcrumb').exists()).toBeTruthy()
	})

	it('should render a active item', () => {
		const rendered = shallow(<Breadcrumb breadcrumbs={breadcrumbs} />)
		expect(rendered.find('.breadcrumb-item.active').exists()).toBeTruthy()
	})
})
