import React, { useState } from 'react'
import { Formik } from 'formik'
import * as yup from 'yup'
import { useAsync, useToggle } from 'react-use'
import { NavLink } from 'react-router-dom'
import Button from '~/components/ui/button/Button'

import { DollyApi } from '~/service/Api'
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import ModalActionKnapper from '~/components/ui/modal/ModalActionKnapper'

import './nyIdent.less'
import styled from 'styled-components'
import Alertstripe from 'nav-frontend-alertstriper'
import _get from 'lodash/get'
import _has from 'lodash/has'
import { tpsfAttributter } from '~/components/bestillingsveileder/utils'

const initialValues = {
	antall: 1,
	identtype: Options('identtype')[0].value,
	mal: null,
}

const StyledH3 = styled.h3`
	margin-bottom: 10px;
`

const InputDiv = styled.div`
	margin-top: 20px;
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

export const NyIdent = ({ onAvbryt, onSubmit, zBruker }) => {
	const [zIdent, setZIdent] = useState(zBruker)
	const [malAktiv, toggleMalAktiv] = useToggle(false)
	const state = useAsync(async () => {
		const response = await DollyApi.getBestillingMaler()
		return response.data
	}, [])

	const zIdentOptions = state.value ? getZIdentOptions(state.value.malbestillinger) : []
	const malOptions = state.value ? getMalOptions(state.value.malbestillinger, zIdent) : []

	const handleMalChange = (formikbag) => {
		toggleMalAktiv()
		if (formikbag.values.mal) {
			formikbag.setFieldValue('mal', null)
		}
	}

	const handleBrukerChange = (event, formikbag) => {
		setZIdent(event.value)
		formikbag.setFieldValue('mal', null)
	}

	const preSubmit = (values, formikBag) => {
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
					<div className="ny-ident-form">
						<h3>Velg type og antall</h3>
						<div className="ny-ident-form_selects">
							<FormikSelect
								name="identtype"
								label="Velg identtype"
								size="medium"
								options={Options('identtype')}
								isClearable={false}
							/>
							<FormikTextInput name="antall" label="Antall" type="number" size="medium" />
						</div>
						<div className="ny-ident-form_maler">
							<div>
								<StyledH3>Opprett fra mal</StyledH3>
								<DollyCheckbox
									name="aktiver-maler"
									onChange={() => handleMalChange(formikBag)}
									label="Vis"
									isSwitch
								/>
							</div>

							<InputDiv>
								<DollySelect
									name="zIdent"
									label="Bruker"
									isLoading={state.loading}
									options={zIdentOptions}
									size="medium"
									onChange={(e) => handleBrukerChange(e, formikBag)}
									value={zIdent}
									isClearable={false}
									disabled={!malAktiv}
								/>
								<FormikSelect
									name="mal"
									label="Maler"
									isLoading={state.loading}
									options={malOptions}
									size="grow"
									fastfield={false}
									disabled={!malAktiv}
								/>
							</InputDiv>
							{erTpsfMal && (
								<Alertstripe type={'advarsel'} style={{ width: '97%' }}>
									Denne malen utdatert, og vil dessverre ikke fungere som den skal. Dette fordi
									master for bestillinger er endret fra TPS til PDL. Vi anbefaler at du oppretter en
									ny mal og sletter denne malen.
								</Alertstripe>
							)}
							<div className="mal-admin">
								<Button kind="maler" onClick={() => {}}>
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

const getZIdentOptions = (malbestillinger) =>
	Object.keys(malbestillinger).map((ident) => ({
		value: ident,
		label: ident,
	}))

const getMalOptions = (malbestillinger, zIdent) => {
	if (!malbestillinger || !malbestillinger[zIdent]) return []
	return malbestillinger[zIdent].map((mal) => ({
		value: mal.id,
		label: mal.malNavn,
		data: { bestilling: mal.bestilling, malNavn: mal.malNavn },
	}))
}
