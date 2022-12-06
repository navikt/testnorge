import React, { useState } from 'react'
import { Formik, FormikProps } from 'formik'
import * as yup from 'yup'
import { useToggle } from 'react-use'
import { NavLink } from 'react-router-dom'
import Button from '@/components/ui/button/Button'
import { DollySelect, FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { DollyCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Alert } from '@navikt/ds-react'
import ModalActionKnapper from '@/components/ui/modal/ModalActionKnapper'

import styled from 'styled-components'
import _get from 'lodash/get'
import _has from 'lodash/has'
import { tpsfAttributter } from '@/components/bestillingsveileder/utils'
import { Mal, useDollyMaler } from '@/utils/hooks/useMaler'

const initialValues = {
	antall: 1,
	identtype: Options('identtype')[0].value,
	mal: null as string,
}

export type NyBestillingProps = {
	brukernavn: any
	onSubmit: (arg0: any, arg1: any) => any
	onAvbryt: () => void
}

const InputDiv = styled.div`
	margin-top: 10px;
`

const validationSchema = yup.object({
	antall: yup
		.number()
		.positive('Må være et positivt tall')
		.min(1, 'Må minst opprette ${min} person')
		.max(50, 'Kan kun bestille max 50 identer om gangen.')
		.required('Oppgi antall personer'),
	identtype: yup.string().required('Velg en identtype'),
})

export const NyIdent = ({ brukernavn, onAvbryt, onSubmit }: NyBestillingProps) => {
	const [bruker, setBruker] = useState(brukernavn)
	const [malAktiv, toggleMalAktiv] = useToggle(false)
	const { maler, loading } = useDollyMaler()

	const brukerOptions = getBrukerOptions(maler)
	const malOptions = getMalOptions(maler, bruker)

	const handleMalChange = (formikbag: FormikProps<any>) => {
		toggleMalAktiv()
		if (formikbag.values.mal) {
			formikbag.setFieldValue('mal', null)
		}
	}

	const handleBrukerChange = (event: { value: any }, formikbag: FormikProps<any>) => {
		setBruker(event.value)
		formikbag.setFieldValue('mal', null)
	}

	const preSubmit = (values: { mal: any }, formikBag: any) => {
		if (values.mal) values.mal = malOptions.find((m) => m.value === values.mal).data
		return onSubmit(values, formikBag)
	}

	return (
		<Formik initialValues={initialValues} validationSchema={validationSchema} onSubmit={preSubmit}>
			{(formikBag) => {
				const valgtMal = malOptions.find((mal) => mal.value === _get(formikBag.values, 'mal'))
				const valgtMalTpsfValues = _get(valgtMal, 'data.bestilling.tpsf')
				const erTpsfMal = tpsfAttributter.some((a) => _has(valgtMalTpsfValues, a))

				return (
					<div className="ny-bestilling-form">
						<h3>Velg type og antall</h3>
						<div className="ny-bestilling-form_selects">
							<FormikSelect
								name="identtype"
								label="Velg identtype"
								size="medium"
								options={Options('identtype')}
								isClearable={false}
							/>
							<FormikTextInput name="antall" label="Antall" type="number" size="medium" />
						</div>
						<div className="ny-bestilling-form_maler">
							<div>
								<DollyCheckbox
									name="aktiver-maler"
									onChange={() => handleMalChange(formikBag)}
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
									onChange={(e) => handleBrukerChange(e, formikBag)}
									value={bruker}
									isClearable={false}
									isDisabled={!malAktiv}
								/>
								<FormikSelect
									name="mal"
									label="Maler"
									isLoading={loading}
									options={malOptions}
									size="grow"
									fastfield={false}
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
								<Button kind="maler">
									<NavLink to="/minside">Administrer maler</NavLink>
								</Button>
							</div>
						</div>
						<ModalActionKnapper
							submitknapp="Start bestilling"
							disabled={!formikBag.isValid || formikBag.isSubmitting}
							onSubmit={formikBag.handleSubmit}
							onAvbryt={onAvbryt}
							center
						/>
					</div>
				)
			}}
		</Formik>
	)
}

export const getBrukerOptions = (malbestillinger: [string, Mal[]]) =>
	Object.keys(malbestillinger).map((ident) => ({
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
