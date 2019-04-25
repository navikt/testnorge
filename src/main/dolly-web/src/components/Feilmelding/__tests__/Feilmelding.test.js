import React from 'react'
import { shallow } from 'enzyme'
import Feilmelding from '../Feilmelding'

describe('Feilmelding.js', () => {
    
    const ingenFeilmeldingBestilling = {
        tpsfStatus: [ 
            {environmentIdents: {t5: ["12345678921"]}, 
            statusMelding: 'OK'
            }
        ]
    }

    const tpsfFeilmeldingBestilling = {
        tpsfStatus: [ 
            {environmentIdents: {t5: ["12345678921"]}, 
            statusMelding: 'Feilmelding 1'
            }
        ] 
    }

    const tpsfFeilmeldingerBestilling = {
        tpsfStatus: [ 
            {environmentIdents: {t5: ["12345678921"]}, 
            statusMelding: 'Feilmelding 1'
            },
            {environmentIdents: {t5: ["12345678921"]}, 
            statusMelding: 'Feilmelding 2'
            }
        ],
        sigrunStubStatus: [
            {
                statusMelding: 'Feilmelding1',
                identer: ["12345678921"]
            }
        ] 
    }
    
	const renderIngenFeilmelding = shallow( <Feilmelding bestilling = {ingenFeilmeldingBestilling}/>)
    const renderTpsfFeilmelding = shallow( <Feilmelding bestilling = {tpsfFeilmeldingBestilling}/>)
    const renderTpsfFeilmeldinger = shallow( <Feilmelding bestilling = {tpsfFeilmeldingerBestilling}/>)

    it('should render ingen feilmeldinger', () => {
        expect(renderIngenFeilmelding.find('.feil-container').exists()).toBe(false)
    })

    it('should not render border line', () => {
        expect(renderTpsfFeilmelding.find('.feil-container_border').exists()).toBe(false)
    })

    it('should render border line', () => {
        expect(renderTpsfFeilmeldinger.find('.feil-container_border').exists()).toBe(true)
    })
    it('should render border line', () => {
        expect(renderTpsfFeilmeldinger.find('#stubNavn')).to.have.lengthOf(11)
    })

    
})