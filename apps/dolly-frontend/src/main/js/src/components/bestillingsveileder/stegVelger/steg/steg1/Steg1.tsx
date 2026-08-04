import React, { useContext, useState } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { Steg1Person } from './Steg1Person'
import { Steg1Organisasjon } from './Steg1Organisasjon'
import { Search } from '@navikt/ds-react'

export const visEgenskap = (label, filterText) => {
	return !filterText || label?.toLowerCase().includes(filterText.toLowerCase())
}

const Steg1 = ({ stateModifier }) => {
	const opts: any = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	const [filterText, setFilterText] = useState('')

	return (
		<>
			<search style={{ marginBottom: '20px' }}>
				<Search
					label="Filtrer egenskaper ..."
					placeholder="Filtrer egenskaper ..."
					variant="simple"
					onChange={(e) => setFilterText(e)}
				/>
			</search>
			{opts?.is?.nyOrganisasjon ||
			opts?.is?.nyStandardOrganisasjon ||
			opts?.is?.nyOrganisasjonFraMal ? (
				<Steg1Organisasjon stateModifier={stateModifier} />
			) : (
				<Steg1Person stateModifier={stateModifier} filterText={filterText} />
			)}
		</>
	)
}

export default Steg1
