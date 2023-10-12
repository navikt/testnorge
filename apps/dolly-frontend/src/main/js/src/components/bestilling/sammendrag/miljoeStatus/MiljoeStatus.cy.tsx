import React from 'react'
import MiljoeStatus from './MiljoeStatus'
import { gjeldendeBrukerMock } from '../../../../../cypress/mocks/BasicMocks'
import styled from 'styled-components'
import ConfettiExplosion from 'react-confetti-explosion'

const StyledConfettiExplosion = styled(ConfettiExplosion)`
	display: unset;
	align-items: center;
	align-content: center;
	align-self: center;
	text-align: center;
`

const mockedBestilling = {
	id: 123,
	antallIdenter: 2,
	antallLevert: 2,
	ferdig: true,
	sistOppdatert: '2023-09-07T07:59:23.058166',
	bruker: gjeldendeBrukerMock,
	gruppeId: 1,
	stoppet: false,
	status: [
		{
			id: 'PDL_FORVALTER',
			navn: 'Persondetaljer',
			statuser: [
				{
					melding: 'OK',
					identer: ['21417327018', '25457315050'],
				},
			],
		},
		{
			id: 'TPS_MESSAGING',
			navn: 'Meldinger til TPS',
			statuser: [
				{
					melding: 'Advarsel: NorskBankkonto: Status ukjent (tidsavbrudd)',
					detaljert: [
						{
							miljo: 'q2',
							identer: ['25457315050'],
						},
					],
				},
				{
					melding: 'OK',
					detaljert: [
						{
							miljo: 'q1',
							identer: ['25457315050', '21417327018'],
						},
						{
							miljo: 'q2',
							identer: ['21417327018'],
						},
						{
							miljo: 'q4',
							identer: ['25457315050', '21417327018'],
						},
						{
							miljo: 't13',
							identer: ['25457315050', '21417327018'],
						},
						{
							miljo: 'qx',
							identer: ['25457315050', '21417327018'],
						},
						{
							miljo: 't3',
							identer: ['25457315050', '21417327018'],
						},
					],
				},
			],
		},
		{
			id: 'KONTOREGISTER',
			navn: 'Bankkontoregister',
			statuser: [
				{
					melding: 'OK',
					identer: ['21417327018', '25457315050'],
				},
			],
		},
	],
}

describe('<MiljoeStatus />', () => {
	it('renders', () => {
		cy.mount(
			<div style={{ display: 'flex', flexDirection: 'column' }}>
				<MiljoeStatus bestilling={mockedBestilling} />
				<StyledConfettiExplosion particleCount={70} force={0.3} duration={2800} />
			</div>,
		)
	})
})
