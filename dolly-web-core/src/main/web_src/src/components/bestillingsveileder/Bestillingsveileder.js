import React, { useState } from 'react'
import _set from 'lodash/fp/set'
import _get from 'lodash/get'
import { Header, headerFromInitialValues } from './Header/Header'
import { StegVelger } from './StegVelger'
import { Steg1 } from './steg/steg1/Steg1'
import { Steg2 } from './steg/Steg2'
import { Steg3 } from './steg/Steg3'
import { stateModifierFns } from './stateModifier'
import { mergeKeepShape } from '~/utils/Merge'

import './bestillingsveileder.less'

const steps = [Steg1, Steg2, Steg3]

const createInitialValues = (locState = {}) => {
	let iv = {
		antall: locState.antall || 1,
		environments: [],
		__lagreSomNyMal: false,
		malBestillingNavn: ''
	}

	if (locState.mal) {
		iv = Object.assign(iv, locState.mal.mal)
	}

	return {
		iv,
		identtype: locState.identtype || 'FNR',
		mal: locState.mal,
		opprettFraIdenter: locState.opprettFraIdenter
	}
}

export const Bestillingsveileder = ({ location, sendBestilling }) => {
	const { identtype, iv, mal, opprettFraIdenter } = createInitialValues(location.state)
	const [initialValues, setInitialValues] = useState(iv)
	const [savedValues, setSavedValues] = useState({})

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
	const headerData = headerFromInitialValues(
		initialValues.antall,
		identtype,
		mal,
		opprettFraIdenter
	)

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
