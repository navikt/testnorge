import { DollyCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { MiljoeInfo } from './MiljoeInfo'

import './MiljoVelger.less'
import styled from 'styled-components'
import { ifPresent } from '@/utils/YupValidations'
import * as Yup from 'yup'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
import Loading from '@/components/ui/loading/Loading'
import { ErrorMessageWithFocus } from '@/utils/ErrorMessageWithFocus'
import { Alert } from '@navikt/ds-react'
import { useFieldArray, useFormContext } from 'react-hook-form'

const StyledH3 = styled.h3`
	display: flex;
	justify-content: flex-start;
	align-items: center;
`

const bankIdQ1 = {
	Q: [
		{
			id: 'q1',
			label: 'Q1',
		},
	],
}

const bankIdQ2 = {
	Q: [
		{
			id: 'q2',
			label: 'Q2',
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

export const MiljoVelger = ({
	bestillingsdata,
	heading,
	bankIdBruker,
	orgTilgang,
	alleredeValgtMiljoe,
}) => {
	const { dollyEnvironments, loading } = useDollyEnvironments()
	const formMethods = useFormContext()
	const fieldMethods = useFieldArray({ control: formMethods.control, name: 'environments' })

	if (loading) {
		return <Loading label={'Laster miljøer...'} />
	}

	const filterEnvironments = (miljoer, erBankIdBruker) => {
		if (erBankIdBruker) {
			const tilgjengeligMiljo = orgTilgang?.miljoe
			if (tilgjengeligMiljo === 'q2') return bankIdQ2
			return bankIdQ1
		}
		return {
			Q: miljoer.Q.filter((env) => env.id !== 'qx' && env.id !== 'q5'),
			T: miljoer.T.filter((env) => env.id !== 't13'),
		}
	}

	const disableAllEnvironments = erMiljouavhengig(bestillingsdata)
	const filteredEnvironments = filterEnvironments(dollyEnvironments, bankIdBruker)
	const order = ['T', 'Q']
	const values = formMethods.watch('environments')

	if (disableAllEnvironments) {
		formMethods.setValue('environments', [])
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

			{order.map((type) => {
				const category = filteredEnvironments[type]
				if (!category) {
					return null
				}

				const isChecked = (id) => values.includes(id)

				const onClick = (e) => {
					const { id } = e.target
					if (!alleredeValgtMiljoe?.includes(id)) {
						isChecked(id) ? fieldMethods.remove(values.indexOf(id)) : fieldMethods.append(id)
					}
				}

				return (
					<fieldset key={type} name={`Liste over ${type}-miljøer`}>
						<StyledH3>{type}-miljøer </StyledH3>
						<div className="miljo-velger_checkboxes">
							{category.map((env) => (
								<DollyCheckbox
									key={env.id}
									id={env.id}
									disabled={
										env.disabled ||
										(disableAllEnvironments && values.length < 1) ||
										alleredeValgtMiljoe.some((miljoe) => miljoe === env.id)
									}
									label={env?.id?.toUpperCase()}
									checked={values.includes(env.id)}
									onClick={onClick}
									size={'small'}
								/>
							))}
						</div>
					</fieldset>
				)
			})}

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
		}),
	),
}
