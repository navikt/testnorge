import React, { useEffect } from 'react'
// @ts-ignore
import useBoolean from '~/utils/hooks/useBoolean'
// @ts-ignore
import Button from '~/components/ui/button/Button'
// @ts-ignore
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
// @ts-ignore
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
// @ts-ignore
import { DollyTextInput, FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
// @ts-ignore
import { PersoninformasjonKodeverk } from '~/config/kodeverk'

import { FormikProps } from 'formik'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import _get from 'lodash/get'

export interface TelefonnummerArray {
	person: {
		telefonnummer: Array<TelefonnummerValues>
	}
}

interface TelefonnummerValues {
	landskode?: string
	nummer?: string
}

interface TelefonnummerProps {
	formikBag: FormikProps<{ pdldata: TelefonnummerArray }>
}

const initialTelefonnummer = {
	landskode: '',
	nummer: '',
	prioritet: 2,
	kilde: 'Dolly',
	master: 'PDL',
	gjeldende: true,
}

const initialTpsTelefonnummer = {
	landkode: '',
	telefonnummer: '',
	telefontype: 'HJET',
}

export const Telefonnummer = ({ formikBag }: TelefonnummerProps) => {
	const tlfListe = _get(formikBag.values, 'pdldata.person.telefonnummer')
	const tlfListeTps = _get(formikBag.values, 'tpsMessaging.telefonnummer')

	if (!tlfListe) return null

	useEffect(() => {
		if (tlfListe.length === 1) {
			formikBag.setFieldValue('pdldata.person.telefonnummer[0].prioritet', 1)
			formikBag.setFieldValue('tpsMessaging.telefonnummer[0].telefontype', 'MOBI')
		}
	}, [tlfListe])

	const optionsPrioritet = () => {
		if (tlfListe.length === 1) {
			return [{ value: 1, label: '1' }]
		} else
			return [
				{ value: 1, label: '1' },
				{ value: 2, label: '2' },
			]
	}

	const handleNewEntry = () => {
		formikBag.setFieldValue('pdldata.person.telefonnummer', [...tlfListe, initialTelefonnummer])
		formikBag.setFieldValue('tpsMessaging.telefonnummer', [...tlfListeTps, initialTpsTelefonnummer])
	}

	const handleRemoveEntry = (idx) => {
		tlfListe.splice(idx, 1)
		tlfListeTps.splice(idx, 1)
		formikBag.setFieldValue('pdldata.person.telefonnummer', tlfListe)
		formikBag.setFieldValue('tpsMessaging.telefonnummer', tlfListeTps)
		//TODO: Stygt - endre??
		formikBag.setFieldValue('pdldata.person.telefonnummer[0].prioritet', 1)
		formikBag.setFieldValue('tpsMessaging.telefonnummer[0].telefontype', 'MOBI')
	}

	const handleChangeLandkode = (value, path, idx) => {
		formikBag.setFieldValue(`${path}.landskode`, value)
		formikBag.setFieldValue(`tpsMessaging.telefonnummer[${idx}].landkode`, value)
	}

	const handleChangeNummer = (target, path, idx) => {
		formikBag.setFieldValue(`${path}.nummer`, target.value)
		formikBag.setFieldValue(`tpsMessaging.telefonnummer[${idx}].telefonnummer`, target.value)
	}

	const handleChangePrioritet = (value, path, idx) => {
		formikBag.setFieldValue(`${path}.prioritet`, value)
		formikBag.setFieldValue(
			`tpsMessaging.telefonnummer[${idx}].telefontype`,
			value === 2 ? 'HJET' : 'MOBI'
		)
	}

	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.telefonnummer'}
				header="Telefonnummer"
				newEntry={initialTelefonnummer}
				disabled={tlfListe.length > 1}
				title={tlfListe.length > 1 ? 'En person kan maksimalt ha to telefonnumre' : null}
				canBeEmpty={false}
				handleNewEntry={handleNewEntry}
				handleRemoveEntry={handleRemoveEntry}
			>
				{(path: string, idx: number) => (
					<>
						<FormikSelect
							name={`${path}.landskode`}
							label="Landkode"
							kodeverk={PersoninformasjonKodeverk.Retningsnumre}
							onChange={({ value }) => handleChangeLandkode(value, path, idx)}
							size="large"
							isClearable={false}
						/>
						<DollyTextInput
							name={`${path}.nummer`}
							label="Telefonnummer"
							onChange={({ target }) => handleChangeNummer(target, path, idx)}
							value={_get(formikBag.values, `${path}.nummer`)}
							size="large"
						/>
						<FormikSelect
							name={`${path}.prioritet`}
							label="Prioritet"
							options={optionsPrioritet()}
							onChange={({ value }) => handleChangePrioritet(value, path, idx)}
							size="xsmall"
							isClearable={false}
						/>
						<AvansertForm path={path} kanVelgeMaster={false} />
					</>
				)}
			</FormikDollyFieldArray>
		</div>
	)
}
