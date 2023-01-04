import React, { useEffect, useState } from 'react'
import * as _ from 'lodash-es'
import { organisasjonPaths } from '../paths'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { OrganisasjonKodeverk } from '@/config/kodeverk'
import { FormikProps } from 'formik'
import { EnhetBestilling } from '../../types'
import { Kontaktdata } from './Kontaktdata'
import { Adresser } from './Adresser'
import { ToggleGroup } from '@navikt/ds-react'
import styled from 'styled-components'

type DetaljerProps = {
	formikBag: FormikProps<{ organisasjon: EnhetBestilling }>
	path: string
	level: number
	number?: string
	maaHaUnderenhet?: boolean
}

enum TypeUnderenhet {
	JURIDISKENHET = 'JURIDISKENHET',
	VIRKSOMHET = 'VIRKSOMHET',
}

const StyledToggleGroup = styled(ToggleGroup)`
	margin-bottom: 7px;
`

export const Detaljer = ({
	formikBag,
	path,
	level,
	number,
	maaHaUnderenhet = true,
}: DetaljerProps) => {
	const initialValues = _.omit(formikBag.values.organisasjon, ['underenheter', 'sektorkode'])
	const underenheter = formikBag.values?.organisasjon?.underenheter
	const sektorkodeErValgt = formikBag.values.organisasjon.hasOwnProperty('sektorkode')

	useEffect(() => {
		if (level === 0 && !_.get(formikBag, `${path}.underenheter`)) {
			formikBag.setFieldValue(`${path}.underenheter`, underenheter || [initialValues])
		}
	}, [])

	const [typeUnderenhet, setTypeUnderenhet] = useState(
		level === 0 ||
			(_.has(formikBag.values, `${path}.underenheter`) &&
				_.get(formikBag.values, `${path}.underenheter`))
			? TypeUnderenhet.JURIDISKENHET
			: TypeUnderenhet.VIRKSOMHET
	)

	const handleToggleChange = (value: TypeUnderenhet) => {
		setTypeUnderenhet(value)
		formikBag.setFieldValue(`${path}.enhetstype`, '')
		if (value === TypeUnderenhet.VIRKSOMHET) {
			formikBag.setFieldValue(`${path}.underenheter`, undefined)
			sektorkodeErValgt && formikBag.setFieldValue(`${path}.sektorkode`, undefined)
		} else if (value === TypeUnderenhet.JURIDISKENHET && level < 4) {
			formikBag.setFieldValue(`${path}.underenheter`, [initialValues])
			sektorkodeErValgt && formikBag.setFieldValue(`${path}.sektorkode`, '')
		}
	}

	return (
		<>
			<Kategori title={!number ? 'Organisasjon' : null} vis={organisasjonPaths} flexRow={true}>
				<div className="toggle--wrapper">
					{level > 0 && (
						<StyledToggleGroup size={'small'} onChange={handleToggleChange} value={typeUnderenhet}>
							<ToggleGroup.Item
								key={TypeUnderenhet.JURIDISKENHET}
								value={TypeUnderenhet.JURIDISKENHET}
							>
								Juridisk enhet
							</ToggleGroup.Item>
							<ToggleGroup.Item key={TypeUnderenhet.VIRKSOMHET} value={TypeUnderenhet.VIRKSOMHET}>
								Virksomhet
							</ToggleGroup.Item>
						</StyledToggleGroup>
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
				canBeEmpty={!maaHaUnderenhet || _.get(formikBag, `values.${path}.enhetstype`) === 'ENK'}
				tag={number}
				isOrganisasjon={true}
			>
				{(path: string, idx: number, _curr: any, number: string) => {
					return (
						<Detaljer
							key={idx}
							formikBag={formikBag}
							path={path}
							level={level + 1}
							number={number ? number : (level + 1).toString()}
							maaHaUnderenhet={
								typeUnderenhet === 'JURIDISKENHET' &&
								_.get(formikBag, `values.${path}.enhetstype`) !== 'ENK'
							}
						/>
					)
				}}
			</FormikDollyFieldArray>
		</>
	)
}
