import React, { useState } from 'react'
import { Formik } from 'formik'
import * as yup from 'yup'
import { useToggle } from 'react-use'
import _get from 'lodash/get'
import { useAsync } from 'react-use'
import { NavLink } from 'react-router-dom'
import Button from '~/components/ui/button/Button'

import { DollyApi } from '~/service/Api'
import { FormikSelect, DollySelect } from '~/components/ui/form/inputs/select/Select'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { ModalActions } from '../ModalActions'

import './nyIdent.less'

const initialValues = {
	antall: 1,
	identtype: Options('identtype')[0].value,
	mal: null
}

const validationSchema = yup.object({
	antall: yup
		.number()
		.positive('Må være et positivt tall')
		.min(1, 'Må minst opprette ${min} person')
		.required('Oppgi antall personer'),
	identtype: yup.string().required('Velg en identtype')
})

export const NyIdent = ({ onAvbryt, onSubmit, zBruker }) => {
	const [zIdent, setZIdent] = useState(zBruker)
	const [malAktiv, toggleMalAktiv] = useToggle(false)
	const state = useAsync(async () => {
		const response = await DollyApi.getBestillingMaler()
		return response.data
	}, [])

	const zIdentOptions = state.value ? getZIdentOptions(state.value.malbestillinger, zBruker) : []
	const malOptions = state.value ? getMalOptions(state.value.malbestillinger, zIdent) : []

	const preSubmit = (values, formikBag) => {
		if (values.mal) values.mal = malOptions.find(m => m.value === values.mal).data
		return onSubmit(values, formikBag)
	}
	return (
		<Formik initialValues={initialValues} validationSchema={validationSchema} onSubmit={preSubmit}>
			{formikBag => (
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
						<div className="ny-ident-form_maler_header">
							<h3>Maler</h3>
							<DollyCheckbox name="aktiver-maler" onChange={toggleMalAktiv} label="Vis" isSwitch />
						</div>

						<div>
							<DollySelect
								name="zIdent"
								label="Z-ident"
								isLoading={state.loading}
								options={zIdentOptions}
								size="medium"
								onChange={e => setZIdent(e.value)}
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
						</div>
						<div className="mal-admin">
							<Button kind="maler" onClick={() => {}}>
								<NavLink to="/minside">Administrer maler</NavLink>
							</Button>
						</div>
					</div>
					<ModalActions
						disabled={!formikBag.isValid || formikBag.isSubmitting}
						onSubmit={formikBag.handleSubmit}
						onAvbryt={onAvbryt}
					/>
				</div>
			)}
		</Formik>
	)
}

const getZIdentOptions = (malbestillinger, zBruker) =>
	Object.keys(malbestillinger).map(ident => ({
		value: ident,
		label: ident === zBruker ? `${ident} (din)` : ident
	}))

const getMalOptions = (malbestillinger, zIdent) => {
	if (!malbestillinger || !malbestillinger[zIdent]) return []
	return malbestillinger[zIdent].map(mal => ({
		value: mal.id,
		label: mal.malNavn,
		data: { bestilling: mal.bestilling, malNavn: mal.malNavn }
	}))
}
