import { DollyCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { MiljoeInfo } from './MiljoeInfo'
import { useEffect } from 'react'
import './MiljoVelger.less'
import styled from 'styled-components'
import { ifPresent } from '@/utils/YupValidations'
import * as Yup from 'yup'
import Loading from '@/components/ui/loading/Loading'
import { DollyErrorMessageWrapper } from '@/utils/DollyErrorMessageWrapper'
import { Alert } from '@navikt/ds-react'
import { useFormContext, useWatch } from 'react-hook-form'

const StyledH3 = styled.h3`
	display: flex;
	justify-content: flex-start;
	align-items: center;
`

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

const erMiljouavhengig = (bestilling: Record<string, unknown> | undefined) => {
	if (!bestilling) return false
	let miljoeNotRequired = true
	miljoeavhengig.forEach((system) => {
		if (bestilling.hasOwnProperty(system)) {
			miljoeNotRequired = false
		}
	})
	return miljoeNotRequired
}

interface MiljoVelgerProps {
	bestillingsdata?: Record<string, unknown>
	heading: string
	currentBruker: any
	gyldigeMiljoer: any[]
	tilgjengeligeMiljoer?: string
	loading: boolean
}

export const MiljoVelger = ({
	bestillingsdata,
	heading,
	currentBruker,
	gyldigeMiljoer,
	tilgjengeligeMiljoer,
	loading = false,
}: MiljoVelgerProps) => {
	const formMethods = useFormContext()
	const values = useWatch({ name: 'environments', control: formMethods.control }) || []

	const disableAllEnvironments = erMiljouavhengig(bestillingsdata)

	useEffect(() => {
		if (disableAllEnvironments && values.length > 0) {
			formMethods.setValue('environments', [])
			formMethods.trigger('environments')
		}
	}, [disableAllEnvironments, values, formMethods])

	if (loading) {
		return <Loading label={'Laster miljøer ...'} />
	}

	const bankIdBruker = currentBruker?.brukertype === 'BANKID'
	const tilgjengeligeMiljoerArray = bankIdBruker
		? tilgjengeligeMiljoer
			? tilgjengeligeMiljoer.split(',')
			: ['q1']
		: null

	const filteredEnvironments = gyldigeMiljoer?.filter(
		(env: any) => !tilgjengeligeMiljoerArray || tilgjengeligeMiljoerArray.includes(env.id),
	)

	const isChecked = (id: string) => values.includes(id)

	const toggleEnvironment = (id: string) => {
		const next = isChecked(id) ? values.filter((value: string) => value !== id) : values.concat(id)
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
					<MiljoeInfo
						bestillingsdata={bestillingsdata}
						dollyEnvironments={filteredEnvironments}
						tilgjengeligeMiljoer={tilgjengeligeMiljoerArray}
					/>
				</>
			)}
			<fieldset name={`Liste over miljøer`}>
				<StyledH3>Miljøer</StyledH3>
				<div className="miljo-velger_checkboxes">
					{filteredEnvironments?.map((env: any) => (
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
