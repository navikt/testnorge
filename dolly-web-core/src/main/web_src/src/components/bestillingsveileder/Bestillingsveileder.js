import React, { useState } from 'react'
import { getInitialValues } from '~/service/attributter/Attributter'
import { StegVelger } from './StegVelger'
import { Steg1 } from './steg/Steg1'
import { Steg2 } from './steg/Steg2'
import { mergeKeepShape } from '~/utils/Merge'

import './bestillingsveileder.less'

const steps = [Steg1, Steg2]

export const Bestillingsveileder = () => {
	const [attributter, setAttributter] = useState(getInitialValues())
	const [savedValues, setSavedValues] = useState({})

	const checkAttributter = attrs => setAttributter(Object.assign({}, attributter, attrs))

	const handleSubmit = (values, formikBag) => {
		console.log(values)
	}

	const initialValuesSteps = steps.reduce(
		(acc, curr) => Object.assign({}, acc, curr.initialValues(attributter)),
		{}
	)

	// Merge with savedValues
	const initialValues = mergeKeepShape(initialValuesSteps, savedValues)

	return (
		<div className="bestillingsveileder">
			<StegVelger
				steps={steps}
				initialValues={initialValues}
				copyValues={setSavedValues}
				onSubmit={handleSubmit}
			>
				{(CurrentStep, formikBag) => (
					<CurrentStep
						formikBag={formikBag}
						attributter={attributter}
						checkAttributter={checkAttributter}
					/>
				)}
			</StegVelger>
		</div>
	)
}
