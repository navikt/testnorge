import React from 'react'
import _set from 'lodash/fp/set'
import _get from 'lodash/get'
import Formatter from '~/utils/DataFormatter'
import { Header } from '~/components/ui/header/Header'
import { StegVelger } from './StegVelger'
import { Steg1 } from './steg/steg1/Steg1'
import { Steg2 } from './steg/Steg2'
import { Steg3 } from './steg/Steg3'

import './bestillingsveileder.less'

const steps = [Steg1, Steg2, Steg3]

const createInitialValues = (locState = {}) => {
	let initialValues = {
		antall: locState.antall || 1,
		environments: [],
		__lagreSomNyMal: false,
		malBestillingNavn: ''
	}

	if (locState.mal) {
		initialValues = Object.assign(initialValues, locState.mal.mal)
	}

	return {
		initialValues,
		identtype: locState.identtype || 'FNR',
		mal: locState.mal,
		opprettFraIdenter: locState.opprettFraIdenter
	}
}

export const Bestillingsveileder = ({ location, sendBestilling }) => {
	const { initialValues, identtype, mal, opprettFraIdenter } = createInitialValues(location.state)

	const handleSubmit = (values, formikBag) => {
		// props.createBestillingMal(values.malNavn) //Nå sjekkes ikke malnavn

		// Sett identType (denne blir ikke satt tidligere grunnet at den sitter inne i tpsf-noden)
		values = _set('tpsf.identtype', identtype, values)

		sendBestilling(values)
	}

	const antall = (opprettFraIdenter && opprettFraIdenter.length) || initialValues.antall

	return (
		<div className="bestillingsveileder">
			<StegVelger steps={steps} initialValues={initialValues} onSubmit={handleSubmit}>
				{(CurrentStep, formikBag, stateModifier) => (
					<React.Fragment>
						<Header>
							<div className="flexbox">
								<Header.TitleValue
									title="Antall"
									value={`${antall} ${antall > 1 ? 'personer' : 'person'}`}
								/>
								{!opprettFraIdenter && <Header.TitleValue title="Identtype" value={identtype} />}
								{opprettFraIdenter && (
									<Header.TitleValue
										title="Opprett fra eksisterende personer"
										value={Formatter.arrayToString(opprettFraIdenter)}
									/>
								)}
								{mal && <Header.TitleValue title="Basert på mal" value={mal.malNavn} />}
							</div>
						</Header>

						<CurrentStep formikBag={formikBag} stateModifier={stateModifier} />
					</React.Fragment>
				)}
			</StegVelger>
		</div>
	)
}
