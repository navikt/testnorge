import { DollyCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { MiljoeInfo } from './MiljoeInfo'

import './MiljoVelger.less'
import styled from 'styled-components'
import { ifPresent } from '@/utils/YupValidations'
import * as Yup from 'yup'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
import Loading from '@/components/ui/loading/Loading'
import { DollyErrorMessageWrapper } from '@/utils/DollyErrorMessageWrapper'
import { Alert } from '@navikt/ds-react'
import { useFormContext, useWatch } from 'react-hook-form'
import { useEffect } from 'react'

const StyledH3 = styled.h3`
	display: flex;
	justify-content: flex-start;
	align-items: center;
`

const bankIdQ1 = {
	id: 'q1',
	label: 'Q1',
}

const bankIdQ2 = {
	id: 'q2',
	label: 'Q2',
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
	const formMethods = useFormContext()

	if (loading) {
		return <Loading label={'Laster miljøer...'} />
	}

	const filterEnvironments = (miljoer, erBankIdBruker) => {
		if (erBankIdBruker) {
			const bankMiljoer = []
			for (let i = 0; i < alleredeValgtMiljoe.length; i++) {
				switch (alleredeValgtMiljoe[i]) {
					case 'q1':
						bankMiljoer.push(bankIdQ1)
						break
					case 'q2':
						bankMiljoer.push(bankIdQ2)
						break
				}
			}
			return bankMiljoer
		}
		return miljoer.Q.filter((env: any) => env.id !== 'qx')
	}

	const disableAllEnvironments = erMiljouavhengig(bestillingsdata)
	const filteredEnvironments = filterEnvironments(dollyEnvironments, bankIdBruker)
	const values = useWatch({ name: 'environments', control: formMethods.control }) || []

	useEffect(() => {
		if (disableAllEnvironments && values.length > 0) {
			formMethods.setValue('environments', [])
			formMethods.trigger('environments')
		}
	}, [disableAllEnvironments, values, formMethods])

	const isChecked = (id) => values.includes(id)

	const toggleEnvironment = (id: string) => {
		const next = isChecked(id) ? values.filter((value) => value !== id) : values.concat(id)
		formMethods.setValue('environments', next)
		formMethods.trigger('environments')
	}

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
			<fieldset name={`Liste over miljøer`}>
				<StyledH3>Miljøer</StyledH3>
				<div className="miljo-velger_checkboxes">
					{filteredEnvironments.map((env: any) => (
						<DollyCheckbox
							key={env.id}
							id={env.id}
							disabled={env.disabled || (disableAllEnvironments && values.length < 1)}
							label={env?.id?.toUpperCase()}
							checked={values.includes(env.id)}
							onChange={() => toggleEnvironment(env.id)}
							size={'small'}
						/>
					))}
				</div>
			</fieldset>
			<DollyErrorMessageWrapper name="environments" />
		</div>
	)
}

MiljoVelger.validation = {
	environments: ifPresent(
		'$environments',
		Yup.array().test('har-miljoe-nar-pakrevd', 'Velg minst ett miljø', (_val, context) => {
			const values = context.parent
			const miljoeNotRequired = erMiljouavhengig(values)
			const hasEnvironments = values.environments.length > 0
			return miljoeNotRequired || hasEnvironments
		}),
	),
}
