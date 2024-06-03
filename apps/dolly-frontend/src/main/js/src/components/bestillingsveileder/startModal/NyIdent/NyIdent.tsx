import React, { useState } from 'react'
import * as Yup from 'yup'
import { useToggle } from 'react-use'
import { NavLink } from 'react-router-dom'
import Button from '@/components/ui/button/Button'
import { DollySelect, FormSelect } from '@/components/ui/form/inputs/select/Select'
import { DollyCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Alert } from '@navikt/ds-react'
import ModalActionKnapper from '@/components/ui/modal/ModalActionKnapper'

import styled from 'styled-components'
import _ from 'lodash'
import { tpsfAttributter } from '@/components/bestillingsveileder/utils'
import { Mal, useDollyMaler } from '@/utils/hooks/useMaler'
import { CypressSelector } from '../../../../../cypress/mocks/Selectors'
import { FormProvider, useForm } from 'react-hook-form'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { yupResolver } from '@hookform/resolvers/yup'

const initialValues = {
	antall: 1,
	identtype: Options('identtype')[0].value,
	mal: null as unknown as string,
}

export type NyBestillingProps = {
	brukernavn: any
	onSubmit: (arg0: any, arg1: any) => any
	onAvbryt: () => void
}

const InputDiv = styled.div`
	margin-top: 10px;
`

const validationSchema = Yup.object({
	antall: Yup.number()
		.positive('Må være et positivt tall')
		.min(1, 'Må minst opprette ${min} person')
		.max(50, 'Kan kun bestille max 50 identer om gangen.')
		.required('Oppgi antall personer'),
	identtype: Yup.string().required('Velg en identtype'),
})

export const NyIdent = ({ brukernavn, onAvbryt, onSubmit }: NyBestillingProps) => {
	const [bruker, setBruker] = useState(brukernavn)
	const [malAktiv, toggleMalAktiv] = useToggle(false)
	const { maler, loading } = useDollyMaler()
	const formMethods = useForm({
		mode: 'onBlur',
		defaultValues: initialValues,
		resolver: yupResolver(validationSchema),
	})

	const brukerOptions = getBrukerOptions(maler)
	const malOptions = getMalOptions(maler, bruker)

	const handleMalChange = (formMethods: UseFormReturn) => {
		toggleMalAktiv()
		if (formMethods.getValues().mal) {
			formMethods.setValue('mal', null)
		}
	}

	const handleBrukerChange = (event: { value: any }, formMethods: UseFormReturn) => {
		setBruker(event.value)
		formMethods.setValue('mal', null)
	}

	const preSubmit = (values: { mal: any }) => {
		if (values.mal) values.mal = malOptions.find((m) => m.value === values.mal).data
		return onSubmit(values, formMethods)
	}

	const valgtMal = malOptions.find((mal) => mal.value === formMethods.watch('mal'))
	const valgtMalTpsfValues = _.get(valgtMal, 'data.bestilling.tpsf')
	const erTpsfMal = tpsfAttributter.some((a) => _.has(valgtMalTpsfValues, a))

	return (
		<FormProvider {...formMethods}>
			<form onSubmit={() => preSubmit(formMethods.getValues())}>
				<div className="ny-bestilling-form">
					<h3>Velg type og antall</h3>
					<div className="ny-bestilling-form_selects">
						<FormSelect
							name="identtype"
							label="Velg identtype"
							size="medium"
							options={Options('identtype')}
							isClearable={false}
						/>
						<FormTextInput name="antall" label="Antall" type="number" size="medium" />
					</div>
					<div className="ny-bestilling-form_maler">
						<div>
							<DollyCheckbox
								data-testid={CypressSelector.TOGGLE_MAL}
								name="aktiver-maler"
								onChange={() => handleMalChange(formMethods)}
								label="Opprett fra mal"
								wrapperSize={'none'}
								size={'small'}
								isSwitch
							/>
						</div>

						<InputDiv>
							<DollySelect
								name="zIdent"
								label="Bruker"
								isLoading={loading}
								options={brukerOptions}
								size="medium"
								onChange={(e) => handleBrukerChange(e, formMethods)}
								value={bruker}
								isClearable={false}
								isDisabled={!malAktiv}
							/>
							<FormSelect
								name="mal"
								label="Maler"
								isLoading={loading}
								options={malOptions}
								size="grow"
								isDisabled={!malAktiv}
							/>
						</InputDiv>
						{erTpsfMal && (
							<Alert variant={'warning'} style={{ width: '97%' }}>
								Denne malen er utdatert, og vil dessverre ikke fungere som den skal. Dette fordi
								master for bestillinger er endret fra TPS til PDL. Vi anbefaler at du oppretter en
								ny mal og sletter denne malen.
							</Alert>
						)}
						<div className="mal-admin">
							<Button kind="maler" fontSize={'1.2rem'}>
								<NavLink to="/minside">Administrer maler</NavLink>
							</Button>
						</div>
					</div>
					<ModalActionKnapper
						data-testid={CypressSelector.BUTTON_START_BESTILLING}
						submitknapp="Start bestilling"
						disabled={!formMethods.formState.isValid || formMethods.formState.isSubmitting}
						onSubmit={() => preSubmit(formMethods.getValues())}
						onAvbryt={onAvbryt}
						center
					/>
				</div>
			</form>
		</FormProvider>
	)
}

export const getBrukerOptions = (malbestillinger: [string, Mal[]]) =>
	malbestillinger &&
	Object.keys(malbestillinger)?.map((ident) => ({
		value: ident,
		label: ident,
	}))

export const getMalOptions = (malbestillinger: [string, Mal[]], bruker: string | number) => {
	if (!malbestillinger || !malbestillinger[bruker]) return []
	return malbestillinger[bruker].map((mal: { id: any; malNavn: any; bestilling: any }) => ({
		value: mal.id,
		label: mal.malNavn,
		data: { bestilling: mal.bestilling, malNavn: mal.malNavn },
	}))
}
