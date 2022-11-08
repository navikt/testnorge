import React from 'react'
import { Alert, Tabs } from '@navikt/ds-react'

export const MiljoTabs = ({ miljoListe, bestilteMiljoer, forsteMiljo, data, children }) => {
	return (
		<Tabs size="small" defaultValue="q1">
			<Tabs.List>
				{/*<Tabs.Tab value="q1" label="Q1" icon={<Icon kind={'feedback-check-circle'} />} />*/}
				<Tabs.Tab value="q1" label="Q1" style={{ color: '#06893a', fontWeight: 'bold' }} />
				<Tabs.Tab value="q2" label="Q2" />
				{miljoListe.forEach((miljo) => {
					return <Tabs.Tab value={miljo} label={miljo.toUpperCase()} />
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
