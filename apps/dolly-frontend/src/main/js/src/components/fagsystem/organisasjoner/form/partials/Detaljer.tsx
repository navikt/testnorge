import React, { useEffect, useState } from 'react'
import * as _ from 'lodash'
import { organisasjonPaths } from '../paths'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { OrganisasjonKodeverk } from '@/config/kodeverk'
import { Kontaktdata } from './Kontaktdata'
import { Adresser } from './Adresser'
import { ToggleGroup } from '@navikt/ds-react'
import styled from 'styled-components'
import { UseFormReturn } from 'react-hook-form/dist/types'

type DetaljerProps = {
	formMethods: UseFormReturn
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
	formMethods,
	path,
	level,
	number,
	maaHaUnderenhet = true,
}: DetaljerProps) => {
	const initialValues = _.omit(formMethods.getValues().organisasjon, ['underenheter', 'sektorkode'])
	const underenheter = formMethods.getValues()?.organisasjon?.underenheter
	const sektorkodeErValgt = formMethods.getValues().organisasjon.hasOwnProperty('sektorkode')

	useEffect(() => {
		if (level === 0 && !_.get(formMethods.getValues(), `${path}.underenheter`)) {
			formMethods.setValue(`${path}.underenheter`, underenheter || [initialValues])
		}
	}, [])

	const [typeUnderenhet, setTypeUnderenhet] = useState(
		level === 0 ||
			(_.has(formMethods.getValues(), `${path}.underenheter`) &&
				_.get(formMethods.getValues(), `${path}.underenheter`))
			? TypeUnderenhet.JURIDISKENHET
			: TypeUnderenhet.VIRKSOMHET,
	)

	const handleToggleChange = (value: TypeUnderenhet) => {
		setTypeUnderenhet(value)
		formMethods.setValue(`${path}.enhetstype`, '')
		if (value === TypeUnderenhet.VIRKSOMHET) {
			formMethods.setValue(`${path}.underenheter`, undefined)
			sektorkodeErValgt && formMethods.setValue(`${path}.sektorkode`, undefined)
		} else if (value === TypeUnderenhet.JURIDISKENHET && level < 4) {
			formMethods.setValue(`${path}.underenheter`, [initialValues])
			sektorkodeErValgt && formMethods.setValue(`${path}.sektorkode`, '')
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

			<Adresser formMethods={formMethods} path={path} />

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
				canBeEmpty={
					!maaHaUnderenhet || _.get(formMethods.getValues(), `${path}.enhetstype`) === 'ENK'
				}
				tag={number}
				isOrganisasjon={true}
			>
				{(path: string, idx: number, _curr: any, number: string) => {
					return (
						<Detaljer
							key={idx}
							formMethods={formMethods}
							path={path}
							level={level + 1}
							number={number ? number : (level + 1).toString()}
							maaHaUnderenhet={
								typeUnderenhet === 'JURIDISKENHET' &&
								_.get(formMethods.getValues(), `${path}.enhetstype`) !== 'ENK'
							}
						/>
					)
				}}
			</FormikDollyFieldArray>
		</>
	)
}
