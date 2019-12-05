import React, { useState } from 'react'
import _set from 'lodash/fp/set'
import _get from 'lodash/get'
import { Header, headerFromInitialValues } from './Header/Header'
import { StegVelger } from './StegVelger'
import { Steg1 } from './steg/steg1/Steg1'
import { Steg2 } from './steg/Steg2'
import { Steg3 } from './steg/Steg3'
import { stateModifierFns } from './initialValues'
import { mergeKeepShape } from '~/utils/Merge'

import './bestillingsveileder.less'

const steps = [Steg1, Steg2, Steg3]

const createInitialValues = (locState = {}) => {
	const base = {
		antall: locState.antall || 1,
		environments: []
	}

	return base
}

export const Bestillingsveileder = ({ location, sendBestilling }) => {
	const [initialValues, setInitialValues] = useState(createInitialValues(location.state))
	const [savedValues, setSavedValues] = useState({})

	const identtype = _get(location, 'state.identtype', 'FNR')

	const handleSubmit = (values, formikBag) => {
		// props.createBestillingMal(values.malNavn) //Nå sjekkes ikke malnavn

		// Sett identType (denne blir ikke satt tidligere grunnet at den sitter inne i tpsf-noden)
		values = _set('tpsf.identtype', identtype, values)

		sendBestilling(values)
	}

	// Merge with savedValues
	const initial = mergeKeepShape(initialValues, savedValues)

	const stateModifier = stateModifierFns(initialValues, setInitialValues)

	// Denne er litt verbos nå, men må nok endre litt etterhvert hvor disse data kommer fra
	const headerData = headerFromInitialValues(initialValues.antall, identtype, null, null)

	return (
		<div className="bestillingsveileder">
			<StegVelger
				steps={steps}
				initialValues={initial}
				copyValues={setSavedValues}
				onSubmit={handleSubmit}
			>
				{(CurrentStep, formikBag) => (
					<React.Fragment>
						<Header data={headerData} />
						<CurrentStep formikBag={formikBag} stateModifier={stateModifier} />
					</React.Fragment>
				)}
			</StegVelger>
		</div>
	)
}
