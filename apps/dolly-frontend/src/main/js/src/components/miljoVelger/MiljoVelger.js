import React from 'react'
import { FieldArray } from 'formik'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { MiljoeInfo } from './MiljoeInfo/MiljoeInfo'

import './MiljoVelger.less'
import styled from 'styled-components'
import { ifPresent } from '~/utils/YupValidations'
import * as Yup from 'yup'
import { useDollyEnvironments } from '~/utils/hooks/useEnvironments'
import Loading from '~/components/ui/loading/Loading'
import { ErrorMessageWithFocus } from '~/utils/ErrorMessageWithFocus'
import { Alert } from '@navikt/ds-react'

const StyledH3 = styled.h3`
	display: flex;
	justify-content: flex-start;
	align-items: center;
`

const bankIdMiljoer = {
	Q: [
		{
			id: 'q1',
			label: 'Q1',
		},
	],
}

const miljoeavhengig = [
	'aareg',
	'pensjonforvalter',
	'inntektsmelding',
	'arenaforvalter',
	'sykemelding',
	'instdata',
	'dokarkiv',
	'organisasjon',
	'underenheter',
]

const erMiljouavhengig = (bestilling) => {
	if (!bestilling) return false
	let miljoeNotRequired = true
	miljoeavhengig.forEach((system) => {
		if (bestilling.hasOwnProperty(system)) {
			miljoeNotRequired = false
		}
	})
	return miljoeNotRequired
}

export const MiljoVelger = ({ bestillingsdata, heading, bankIdBruker, alleredeValgtMiljoe }) => {
	const { dollyEnvironments, loading } = useDollyEnvironments()
	if (loading) {
		return <Loading label={'Laster miljøer...'} />
	}

	const filterEnvironments = (miljoer, erOrg, erBankIdBruker) => {
		if (erBankIdBruker) return bankIdMiljoer
		if (!erOrg) return miljoer
		const filtrerteMiljoer = { ...miljoer }
		filtrerteMiljoer.Q = filtrerteMiljoer.Q.filter((env) => !env.id.includes('qx'))
		return filtrerteMiljoer
	}

	const disableAllEnvironments = erMiljouavhengig(bestillingsdata)
	const erOrganisasjon = bestillingsdata?.hasOwnProperty('organisasjon')
	const filteredEnvironments = filterEnvironments(dollyEnvironments, erOrganisasjon, bankIdBruker)

	const order = ['T', 'Q']

	return (
		<div className="miljo-velger">
			<h2>{heading}</h2>
			{bestillingsdata && (
				<>
					{disableAllEnvironments && (
						<Alert variant={'info'}>Denne bestillingen er uavhengig av miljøer.</Alert>
					)}
					<MiljoeInfo bestillingsdata={bestillingsdata} dollyEnvironments={filteredEnvironments} />
				</>
			)}

			<FieldArray name="environments">
				{({ push, remove, form }) => {
					const values = form.values.environments

					const isChecked = (id) => values.includes(id)

					const onClick = (e) => {
						const { id } = e.target
						if (!alleredeValgtMiljoe?.includes(id)) {
							isChecked(id) ? remove(values.indexOf(id)) : push(id)
						}
					}

					return order.map((type) => {
						const category = filteredEnvironments[type]
						if (!category) {
							return null
						}

						return (
							<fieldset key={type} name={`Liste over ${type}-miljøer`}>
								<StyledH3>{type}-miljøer </StyledH3>
								<div className="miljo-velger_checkboxes">
									{category.map((env) => (
										<DollyCheckbox
											key={env.id}
											id={env.id}
											disabled={env.disabled || (disableAllEnvironments && values.length < 1)}
											label={env?.id?.toUpperCase()}
											checked={values.includes(env.id)}
											onClick={onClick}
											size={'small'}
										/>
									))}
								</div>
							</fieldset>
						)
					})
				}}
			</FieldArray>

			<ErrorMessageWithFocus name="environments" className="error-message" component="div" />
		</div>
	)
}

MiljoVelger.validation = {
	environments: ifPresent(
		'$environments',
		Yup.array().test('har-miljoe-nar-pakrevd', 'Velg minst ett miljø', function miljoetest() {
			const values = this.options.context
			const miljoeNotRequired = erMiljouavhengig(values)
			const hasEnvironments = values.environments.length > 0
			return miljoeNotRequired || hasEnvironments
		})
	),
}
