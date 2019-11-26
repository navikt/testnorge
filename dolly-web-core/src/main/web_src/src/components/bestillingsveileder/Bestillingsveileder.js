import React, { useState } from 'react'
import { Header, headerFromInitialValues } from './Header/Header'
import { getInitialValues } from '~/service/attributter/Attributter'
import { StegVelger } from './StegVelger'
import { Steg1 } from './steg/Steg1'
import { Steg2 } from './steg/Steg2'
import { Steg3 } from './steg/Steg3'
import { createInitialValues } from './initialValues'

import './bestillingsveileder.less'

const steps = [Steg1, Steg2, Steg3]

export const Bestillingsveileder = props => {
	const [attributter, setAttributter] = useState(getInitialValues())
	const [savedValues, setSavedValues] = useState({})

	const baseBestilling = props.location.state

	const checkAttributter = attrs => setAttributter(Object.assign({}, attributter, attrs))

	const handleSubmit = (values, formikBag) => {
		props.createBestillingMal(values.malNavn) //Nå sjekkes ikke malnavn
		props.sendBestilling(values)
	}

	const initialValues = createInitialValues(steps, attributter, savedValues, baseBestilling)

	// Denne er litt verbos nå, men må nok endre litt etterhvert hvor disse data kommer fra
	const headerData = headerFromInitialValues(
		baseBestilling.antall,
		baseBestilling.identtype,
		baseBestilling.mal,
		baseBestilling.opprettFraIdenter
	)

	return (
		<div className="bestillingsveileder">
			<StegVelger
				steps={steps}
				initialValues={initialValues}
				copyValues={setSavedValues}
				onSubmit={handleSubmit}
			>
				{(CurrentStep, formikBag) => (
					<React.Fragment>
						<Header data={headerData} />
						<CurrentStep
							formikBag={formikBag}
							attributter={attributter}
							checkAttributter={checkAttributter}
							props={props}
						/>
					</React.Fragment>
				)}
			</StegVelger>
		</div>
	)
}
