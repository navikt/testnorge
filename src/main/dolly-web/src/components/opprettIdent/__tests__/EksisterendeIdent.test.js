import React from 'react'
import { shallow } from 'enzyme'
import EksisterendeIdent from '../EksisterendeIdent'
import { Radio } from 'nav-frontend-skjema'

describe('EksisterendeIdent.js', () => {
	
	const renderSkrivemodus = shallow(
		<EksisterendeIdent eksisterendeIdentListe={[]} ugyldigIdentListe = {[]}/>
    )

    const renderAttributtModus = shallow (
        <EksisterendeIdent eksisterendeIdentListe={["25043679458","31049178459"]} ugyldigIdentListe = {["12345678921","45678912365"]}/>
    )
    
    const renderAttributtModusBareGyldige = shallow (
        <EksisterendeIdent eksisterendeIdentListe={["25043679458","31049178459"]} ugyldigIdentListe = {[]}/>
    )
            
    const renderAttributtModusIngenGyldige = shallow (
        <EksisterendeIdent eksisterendeIdentListe={[]} ugyldigIdentListe = {["25043679458","31049178459"]}/>
    )

    it('should render skrivemodus', () => {
        expect(renderSkrivemodus.find('.eksisterende-fnr-inntasting').exists()).toBe(true)
    })
    
    it('should render attributtModus', () => {
        expect(renderAttributtModus.find('.eksisterende-fnr').exists()).toBe(true)
    })

    it('should render attributtModus med bare gyldige fnr', () => {
        expect(renderAttributtModusBareGyldige.find('.ugyldig-container').exists()).toBe(false)
    })

    it('should render attributtModus uten gyldige fnr', () => {
        expect(renderAttributtModusIngenGyldige.find('.ingen-gyldige-identer').exists()).toBe(true)
        expect(renderAttributtModusIngenGyldige.find('.ugyldig').exists()).toBe(true)
    })
})