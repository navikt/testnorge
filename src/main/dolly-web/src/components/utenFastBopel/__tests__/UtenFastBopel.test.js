import React from 'react'
import UtenFastBopel from '../UtenFastBopel'
import { shallow } from 'enzyme'
import Button from '~/components/button/Button'

const item = {
	apiKodeverkId: 'Diskresjonskoder',
	id: 'spesreg',
	inputType: 'select',
	label: 'Diskresjonskoder'
}

describe('UtenFastBopel.js', () => {
	const wrapper = shallow(
		<UtenFastBopel
			values={{ spesreg: 'KLIE', spesreg2: 'KLIE', utenFastBopel: true }}
			key={item.key || item.id}
			item={item}
			valgteVerdier={{ spesreg: '' }}
		/>
	)

	it('should render UtenFastBopel', () => {
		expect(wrapper).toHaveLength(1)
	})
	it('should call componentDidMount', () => {
		const componentDidMountSpy = spyOn(UtenFastBopel.prototype, 'componentDidMount')
		const wrapper = shallow(
			<UtenFastBopel
				values={{ spesreg: 'KLIE', spesreg2: 'KLIE', utenFastBopel: true }}
				key={item.key || item.id}
				item={item}
				valgteVerdier={{ spesreg: '' }}
			/>
		)
		expect(componentDidMountSpy).toBeCalled()
	})
	it('should call _extraComponentProps and return extra prop "loadOptions", with ufb and kode 6', () => {
		const type = 'm_ufb'
		const componentProps = wrapper.instance()._extraComponentProps(item, type)
		expect(componentProps.loadOptions).toBeDefined()
	})
	it('should call _extraComponentProps and return extra prop "loadOptions", without ufb and kode 6', () => {
		const type = 'u_ufb'
		const componentProps = wrapper.instance()._extraComponentProps(item, type)
		expect(componentProps.loadOptions).toBeDefined()
	})
	it('should call _extraComponentPropsKommunenr and return extra prop "options"', () => {
		wrapper
			.instance()
			._extraComponentPropsKommunenr()
			.then(componentProps => {
				expect(componentProps).toBeDefined()
			})
	})
	it('should render button when value of spesreg is set to UFB', () => {
		const wrapper = shallow(
			<UtenFastBopel
				state={{ harEkstraDiskresjonskode: true }}
				values={{ spesreg: 'KLIE', spesreg2: 'KLIE' }}
				key={item.key || item.id}
				item={item}
				valgteVerdier={{ spesreg: 'UFB' }}
			/>
		)
		expect(wrapper.find(Button).exists()).toBeTruthy()
	})
})
