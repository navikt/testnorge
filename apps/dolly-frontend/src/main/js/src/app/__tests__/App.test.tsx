import React from 'react'
import { shallow } from 'enzyme'
import App from '../App'

import Header from '~/components/layout/header/Header'
import Loading from '~/components/ui/loading/Loading'
import Breadcrumb from '~/components/layout/breadcrumb/BreadcrumbWithHoc'

describe('App.tsx', () => {
	const fetchConfig = jest.fn()
	const getCurrentBruker = jest.fn()

	it('should call fetchConfig and getCurrentBruker on mount', async () => {
		const wrapper = shallow(<App fetchConfig={fetchConfig} getCurrentBruker={getCurrentBruker} />)
		await expect(fetchConfig).toBeCalled()
		await expect(getCurrentBruker).toBeCalled()
	})

	it('should render null when no config', () => {
		const wrapper = shallow(<App fetchConfig={fetchConfig} getCurrentBruker={getCurrentBruker} />)
		expect(wrapper.children()).toHaveLength(0)
	})

	it('should render loading component', () => {
		const wrapper = shallow(
			<App fetchConfig={fetchConfig} getCurrentBruker={getCurrentBruker} configReady={true} />
		)

		expect(wrapper.find(Loading).exists()).toBeTruthy()
	})

	// TODO:Test onRedirect

	it('should render main components', () => {
		const personData = { id: 'test' }
		const wrapper = shallow(
			<App
				fetchConfig={fetchConfig}
				getCurrentBruker={getCurrentBruker}
				configReady={true}
				brukerData={personData}
			/>
		)

		expect(wrapper.find(Header).exists()).toBeTruthy()
		expect(wrapper.find(Breadcrumb).exists()).toBeTruthy()
	})
})
