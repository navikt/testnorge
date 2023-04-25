import * as React from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	initialBarn,
	initialForelder,
	initialPdlBiPerson,
	initialPdlPerson,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { FormikProps } from 'formik'
import * as _ from 'lodash-es'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { BarnRelasjon } from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/forelderBarnRelasjon/BarnRelasjon'
import { TypeAnsvarlig } from '@/components/fagsystem/pdlf/PdlTypes'
import { PdlEksisterendePerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlEksisterendePerson'
import { PdlPersonUtenIdentifikator } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonUtenIdentifikator'
import { PdlNyPerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlNyPerson'
import { Alert, ToggleGroup } from '@navikt/ds-react'
import { useEffect } from 'react'

interface ForelderForm {
	formikBag: FormikProps<{}>
	path?: string
	idx?: number
}

type Target = {
	label: string
	value: string
}

const RELASJON_BARN = 'BARN'
const RELASJON_FORELDER = 'FORELDER'

const forelderTyper = ['FORELDER', 'MOR', 'MEDMOR', 'FAR']

export const ForelderBarnRelasjonForm = ({
	formikBag,
	path,
	eksisterendeNyPerson = null,
}: ForelderForm) => {
	const relatertPerson = 'relatertPerson'
	const nyRelatertPerson = 'nyRelatertPerson'
	const relatertPersonUtenFolkeregisteridentifikator =
		'relatertPersonUtenFolkeregisteridentifikator'

	const handleChangeTypeForelderBarn = (target: Target, path: string) => {
		const forelderBarnRelasjon = _.get(formikBag.values, path)
		const forelderBarnClone = _.cloneDeep(forelderBarnRelasjon)

		_.set(forelderBarnClone, 'typeForelderBarn', target?.value || null)
		if (!target) {
			_.set(forelderBarnClone, relatertPerson, undefined)
			_.set(forelderBarnClone, relatertPersonUtenFolkeregisteridentifikator, undefined)
			_.set(forelderBarnClone, nyRelatertPerson, undefined)
		}
		if (target?.value === TypeAnsvarlig.EKSISTERENDE) {
			_.set(forelderBarnClone, relatertPerson, null)
			_.set(forelderBarnClone, relatertPersonUtenFolkeregisteridentifikator, undefined)
			_.set(forelderBarnClone, nyRelatertPerson, undefined)
		}
		if (target?.value === TypeAnsvarlig.UTEN_ID) {
			_.set(forelderBarnClone, relatertPerson, undefined)
			_.set(forelderBarnClone, relatertPersonUtenFolkeregisteridentifikator, initialPdlBiPerson)
			_.set(forelderBarnClone, nyRelatertPerson, undefined)
		}
		if (target?.value === TypeAnsvarlig.NY) {
			_.set(forelderBarnClone, relatertPerson, undefined)
			_.set(forelderBarnClone, relatertPersonUtenFolkeregisteridentifikator, undefined)
			_.set(forelderBarnClone, nyRelatertPerson, initialPdlPerson)
		}

		formikBag.setFieldValue(path, forelderBarnClone)
	}

	const relatertPersonsRolle = forelderTyper.includes(
		_.get(formikBag.values, `${path}.relatertPersonsRolle`)
	)
		? RELASJON_FORELDER
		: RELASJON_BARN

	const erBarn = relatertPersonsRolle === RELASJON_BARN

	const getForelderBarnType = () => {
		const forelderBarnType = _.get(formikBag.values, `${path}.typeForelderBarn`)
		if (forelderBarnType) {
			return forelderBarnType
		} else if (_.get(formikBag.values, `${path}.relatertPerson`)) {
			return 'EKSISTERENDE'
		} else if (_.get(formikBag.values, `${path}.relatertPersonUtenFolkeregisteridentifikator`)) {
			return 'UTEN_ID'
		} else return null
	}

	useEffect(() => {
		if (!_.get(formikBag.values, `${path}.typeForelderBarn`)) {
			formikBag.setFieldValue(`${path}.typeForelderBarn`, getForelderBarnType())
		}
	}, [])

	return (
		<div className="flexbox--flex-wrap">
			{path?.includes('pdldata') ? (
				<div className="toggle--wrapper">
					<ToggleGroup
						onChange={(value: string) => {
							formikBag.setFieldValue(path, value === RELASJON_BARN ? initialBarn : initialForelder)
						}}
						size={'small'}
						defaultValue={relatertPersonsRolle || RELASJON_BARN}
						style={{ backgroundColor: '#ffffff' }}
					>
						<ToggleGroup.Item value={RELASJON_BARN}>{'Barn'}</ToggleGroup.Item>
						<ToggleGroup.Item value={RELASJON_FORELDER}>{'Forelder'}</ToggleGroup.Item>
					</ToggleGroup>
				</div>
			) : (
				<h4 style={{ marginTop: '10px', marginBottom: '15px', width: '100%' }}>
					{erBarn ? 'Barn' : 'Forelder'}
				</h4>
			)}
			<div className="flexbox--flex-wrap">
				{erBarn && <BarnRelasjon formikBag={formikBag} path={path} />}
				{!erBarn && (
					<>
						<FormikSelect
							name={`${path}.relatertPersonsRolle`}
							label="Foreldretype"
							options={Options('foreldreTypePDL')}
							isClearable={false}
						/>
						<FormikCheckbox name={`${path}.borIkkeSammen`} label="Bor ikke sammen" checkboxMargin />
					</>
				)}
				<FormikSelect
					name={`${path}.typeForelderBarn`}
					label={erBarn ? 'Type barn' : 'Type forelder'}
					options={Options('typeAnsvarlig')}
					onChange={(target: Target) => handleChangeTypeForelderBarn(target, path)}
					size="medium"
				/>
			</div>

			{getForelderBarnType() === TypeAnsvarlig.EKSISTERENDE && (
				<PdlEksisterendePerson
					eksisterendePersonPath={`${path}.relatertPerson`}
					label={erBarn ? RELASJON_BARN.toUpperCase() : RELASJON_FORELDER.toUpperCase()}
					formikBag={formikBag}
					eksisterendeNyPerson={eksisterendeNyPerson}
				/>
			)}

			{getForelderBarnType() === TypeAnsvarlig.UTEN_ID && (
				<PdlPersonUtenIdentifikator
					formikBag={formikBag}
					path={`${path}.relatertPersonUtenFolkeregisteridentifikator`}
				/>
			)}

			{getForelderBarnType() === TypeAnsvarlig.NY && (
				<PdlNyPerson nyPersonPath={`${path}.nyRelatertPerson`} formikBag={formikBag} />
			)}

			{!path?.includes('pdldata') && (
				<Alert variant={'info'} size={'small'} style={{ marginTop: '10px', marginBottom: '15px' }}>
					Foreldreansvar kan endres direkte på barnet. For å gjøre dette må barnet importers til
					Dolly, via knapp øverst på denne personen.
				</Alert>
			)}

			<AvansertForm
				path={path}
				kanVelgeMaster={_.get(formikBag.values, `${path}.bekreftelsesdato`) === null}
			/>
		</div>
	)
}

export const ForelderBarnRelasjon = ({ formikBag }: ForelderForm) => {
	return (
		<FormikDollyFieldArray
			name="pdldata.person.forelderBarnRelasjon"
			header={'Relasjon'}
			newEntry={initialBarn}
			canBeEmpty={false}
		>
			{(path: string, idx: number) => {
				return <ForelderBarnRelasjonForm formikBag={formikBag} path={path} />
			}}
		</FormikDollyFieldArray>
	)
}
