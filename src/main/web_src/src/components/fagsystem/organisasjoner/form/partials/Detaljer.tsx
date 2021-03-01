import React, { useEffect, useState } from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import _set from 'lodash/set'
import _omit from 'lodash/omit'
import { organisasjonPaths } from '../paths'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { OrganisasjonKodeverk } from '~/config/kodeverk'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'
import { FormikProps } from 'formik'
import { EnhetBestilling } from '../../types'
import { Kontaktdata } from './Kontaktdata'
import { Adresser } from './Adresser'

type Detaljer = {
	formikBag: FormikProps<{ organisasjon: EnhetBestilling }>
	path: string
	level: number
	number?: string
	maaHaUnderenhet?: boolean
}

enum TypeUnderenhet {
	JURIDISKENHET = 'JURIDISKENHET',
	VIRKSOMHET = 'VIRKSOMHET'
}

export const Detaljer = ({ formikBag, path, level, number, maaHaUnderenhet = true }: Detaljer) => {
	const initialValues = _omit(formikBag.values.organisasjon, ['underenheter', 'sektorkode'])
	initialValues.enhetstype = ''

	const sektorkodeErValgt = formikBag.values.organisasjon.hasOwnProperty('sektorkode')

	const [typeUnderenhet, setTypeUnderenhet] = useState(
		level === 0 ||
			(_has(formikBag.values, `${path}.underenheter`) &&
				_get(formikBag.values, `${path}.underenheter`))
			? TypeUnderenhet.JURIDISKENHET
			: TypeUnderenhet.VIRKSOMHET
	)

	useEffect(() => {
		if (level === 0) {
			formikBag.setFieldValue(`${path}.underenheter`, [initialValues])
		}
	}, [])

	useEffect(() => {
		let values = _set(formikBag.values, `${path}.enhetstype`, '')
		if (typeUnderenhet === TypeUnderenhet.VIRKSOMHET) {
			values = _set(values, `${path}.underenheter`, [])
			if (sektorkodeErValgt) {
				values = _set(values, `${path}.sektorkode`, undefined)
			}
		} else if (typeUnderenhet === TypeUnderenhet.JURIDISKENHET && level < 4) {
			values = _set(values, `${path}.underenheter`, [initialValues])
			if (sektorkodeErValgt) {
				values = _set(values, `${path}.sektorkode`, '')
			}
		}
		formikBag.setValues(values, true)
	}, [typeUnderenhet])

	const handleToggleChange = (event: React.ChangeEvent<any>) => {
		setTypeUnderenhet(event.target.value)
	}

	return (
		<>
			<Kategori title={!number ? 'Organisasjon' : null} vis={organisasjonPaths}>
				<div className="toggle--wrapper">
					{level > 0 && (
						<ToggleGruppe onChange={handleToggleChange} name={path}>
							<ToggleKnapp
								key={TypeUnderenhet.JURIDISKENHET}
								value={TypeUnderenhet.JURIDISKENHET}
								checked={typeUnderenhet === TypeUnderenhet.JURIDISKENHET}
							>
								Juridisk enhet
							</ToggleKnapp>
							<ToggleKnapp
								key={TypeUnderenhet.VIRKSOMHET}
								value={TypeUnderenhet.VIRKSOMHET}
								checked={typeUnderenhet === TypeUnderenhet.VIRKSOMHET}
							>
								Virksomhet
							</ToggleKnapp>
						</ToggleGruppe>
					)}
					<FormikSelect
						name={`${path}.enhetstype`}
						label="Enhetstype"
						kodeverk={
							typeUnderenhet === TypeUnderenhet.JURIDISKENHET
								? OrganisasjonKodeverk.EnhetstyperJuridiskEnhet
								: OrganisasjonKodeverk.EnhetstyperVirksomhet
						}
						size="xxlarge"
						fastfield={false}
						isClearable={false}
					/>
				</div>
				<FormikSelect
					name={`${path}.naeringskode`}
					label="Næringskode"
					kodeverk={OrganisasjonKodeverk.Naeringskoder}
					size="xlarge"
					optionHeight={50}
					isClearable={false}
					visHvisAvhuket
				/>
				{typeUnderenhet === TypeUnderenhet.JURIDISKENHET && (
					<FormikSelect
						name={`${path}.sektorkode`}
						label="Sektorkode"
						kodeverk={OrganisasjonKodeverk.Sektorkoder}
						size="xxlarge"
						isClearable={false}
						visHvisAvhuket
					/>
				)}
				<FormikTextInput name={`${path}.formaal`} label="Formål" size="xlarge" />
				<FormikDatepicker name={`${path}.stiftelsesdato`} label="Stiftelsesdato" />
				<FormikSelect
					name={`${path}.maalform`}
					label="Målform"
					options={Options('maalform')}
					isClearable={false}
					visHvisAvhuket
				/>
			</Kategori>

			<Kontaktdata path={path} />

			<Adresser formikBag={formikBag} path={path} />

			<FormikDollyFieldArray
				name={`${path}.underenheter`}
				header="Underenhet"
				newEntry={initialValues}
				disabled={level > 3 || typeUnderenhet === TypeUnderenhet.VIRKSOMHET}
				title={
					level > 3
						? 'Du kan maksimalt lage fire nivåer av underenheter'
						: typeUnderenhet === TypeUnderenhet.VIRKSOMHET
						? 'Du kan ikke legge til underenheter på enhet av type virksomhet'
						: null
				}
				canBeEmpty={!maaHaUnderenhet || _get(formikBag, `values.${path}.enhetstype`) === 'ENK'}
				tag={number}
				isOrganisasjon={true}
			>
				{(path: string, idx: number, curr: any, number: string) => {
					return (
						<Detaljer
							key={idx}
							formikBag={formikBag}
							path={path}
							level={level + 1}
							number={number ? number : (level + 1).toString()}
							maaHaUnderenhet={
								typeUnderenhet === 'JURIDISKENHET' &&
								_get(formikBag, `values.${path}.enhetstype`) !== 'ENK'
							}
						/>
					)
				}}
			</FormikDollyFieldArray>
		</>
	)
}
