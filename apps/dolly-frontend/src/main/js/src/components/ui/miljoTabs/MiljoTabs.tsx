import React from 'react'
import { Alert, Tabs } from '@navikt/ds-react'
import styled from 'styled-components'

const StyledTab = styled(Tabs.Tab)`
	color: #06893a;
	font-weight: bold;
	&&& {
		.navds-tabs__tab[aria-selected='true'] {
			color: #06893a;
			font-weight: bold;
		}
	}
`

export const MiljoTabs = ({ miljoListe, bestilteMiljoer, forsteMiljo, data, children }) => {
	// console.log('miljoListe: ', miljoListe) //TODO - SLETT MEG
	console.log('bestilteMiljoer miljotabs: ', bestilteMiljoer) //TODO - SLETT MEG
	// console.log('forsteMiljo: ', forsteMiljo) //TODO - SLETT MEG
	console.log('data: ', data) //TODO - SLETT MEG
	return (
		<Tabs size="small" defaultValue={forsteMiljo}>
			<Tabs.List>
				{/*<Tabs.Tab value="q1" label="Q1" icon={<Icon kind={'feedback-check-circle'} />} />*/}
				{/*<Tabs.Tab value="q1" label="Q1" style={{ color: '#06893a', fontWeight: 'bold' }} />*/}
				{/*<Tabs.Tab value="q2" label="Q2" />*/}
				{miljoListe.map((miljo) => {
					if (bestilteMiljoer?.includes(miljo)) {
						return <StyledTab key={miljo} value={miljo} label={miljo.toUpperCase()} />
					}
					return <Tabs.Tab key={miljo} value={miljo} label={miljo.toUpperCase()} />
				})}
			</Tabs.List>
			<Tabs.Panel value="q1" style={{ marginTop: '10px' }}>
				{children}
			</Tabs.Panel>
			<Tabs.Panel value="q2" style={{ marginTop: '10px', marginBottom: '15px' }}>
				<Alert variant="info" size="small" inline>
					Fant ingen pensjon-data i dette miljøet
				</Alert>
			</Tabs.Panel>
		</Tabs>
	)
}
