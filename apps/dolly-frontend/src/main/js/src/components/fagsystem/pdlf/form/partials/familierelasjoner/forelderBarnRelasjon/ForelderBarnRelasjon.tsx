import * as React from 'react'
import { useEffect } from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	getInitialBarn,
	getInitialForelder,
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
import { useContext, useEffect } from 'react'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'

interface ForelderForm {
	formikBag: FormikProps<{}>
	path?: string
	idx?: number
	eksisterendeNyPerson?: any
	identtype?: string
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
	identtype,
}: ForelderForm) => {
	const relatertPerson = 'relatertPerson'
	const nyRelatertPerson = 'nyRelatertPerson'
	const relatertPersonUtenFolkeregisteridentifikator =
		'relatertPersonUtenFolkeregisteridentifikator'

	const handleChangeTypeForelderBarn = (target: Target, path: string) => {
		const forelderBarnRelasjon = _.get(formMethods.getValues(), path)
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

		formMethods.setValue(path, forelderBarnClone)
	}

	const relatertPersonsRolle = forelderTyper.includes(
		_.get(formMethods.getValues(), `${path}.relatertPersonsRolle`),
	)
		? RELASJON_FORELDER
		: RELASJON_BARN

	const erBarn = relatertPersonsRolle === RELASJON_BARN

	const id = _.get(formMethods.getValues(), `${path}.id`)

	const getForelderBarnType = () => {
		const forelderBarnType = _.get(formMethods.getValues(), `${path}.typeForelderBarn`)
		if (forelderBarnType) {
			return forelderBarnType
		} else if (_.get(formMethods.getValues(), `${path}.relatertPerson`)) {
			return 'EKSISTERENDE'
		} else if (
			_.get(formMethods.getValues(), `${path}.relatertPersonUtenFolkeregisteridentifikator`)
		) {
			return 'UTEN_ID'
		} else return null
	}

	useEffect(() => {
		if (!_.get(formMethods.getValues(), `${path}.typeForelderBarn`)) {
			formMethods.setValue(`${path}.typeForelderBarn`, getForelderBarnType())
		}
	}, [])

	return (
		<div className="flexbox--flex-wrap">
			<div className="toggle--wrapper">
				<ToggleGroup
					onChange={(value: string) => {
						formMethods.setValue(
							path,
							value === RELASJON_BARN
								? { ...getInitialBarn(identtype === 'NPID' ? 'PDL' : 'FREG'), id: id }
								: { ...getInitialForelder(identtype === 'NPID' ? 'PDL' : 'FREG'), id: id },
						)
					}}
					size={'small'}
					defaultValue={relatertPersonsRolle || RELASJON_BARN}
					style={{ backgroundColor: '#ffffff' }}
				>
					<ToggleGroup.Item value={RELASJON_BARN} style={{ marginRight: 0 }}>
						{'Barn'}
					</ToggleGroup.Item>
					<ToggleGroup.Item value={RELASJON_FORELDER} style={{ marginRight: 0 }}>
						{'Forelder'}
					</ToggleGroup.Item>
				</ToggleGroup>
			</div>
			<div className="flexbox--flex-wrap">
				{erBarn && <BarnRelasjon formMethods={formMethods} path={path} />}
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
					formMethods={formMethods}
					eksisterendeNyPerson={eksisterendeNyPerson}
				/>
			)}

			{getForelderBarnType() === TypeAnsvarlig.UTEN_ID && (
				<PdlPersonUtenIdentifikator
					formMethods={formMethods}
					path={`${path}.relatertPersonUtenFolkeregisteridentifikator`}
				/>
			)}

			{getForelderBarnType() === TypeAnsvarlig.NY && (
				<PdlNyPerson nyPersonPath={`${path}.nyRelatertPerson`} formMethods={formMethods} />
			)}

			{!path?.includes('pdldata') && erBarn && _.get(formMethods.getValues(), 'harDeltBosted') && (
				<div className="flexbox--full-width">
					<Alert
						variant={'info'}
						size={'small'}
						style={{ marginTop: '10px', marginBottom: '15px' }}
					>
						Delt bosted kan endres direkte på barnet. For å gjøre dette må barnet importeres til
						Dolly, via knapp øverst på denne personen.
					</Alert>
				</div>
			)}

			{!path?.includes('pdldata') && _.get(formMethods.getValues(), 'harForeldreansvar') && (
				<div className="flexbox--full-width">
					<Alert
						variant={'info'}
						size={'small'}
						style={{ marginTop: '10px', marginBottom: '15px' }}
					>
						Foreldreansvar kan endres direkte på barnet. For å gjøre dette må barnet importeres til
						Dolly, via knapp øverst på denne personen.
					</Alert>
				</div>
			)}

			<AvansertForm path={path} kanVelgeMaster={identtype !== 'NPID'} />
		</div>
	)
}

export const ForelderBarnRelasjon = ({ formMethods }: ForelderForm) => {
	const opts = useContext(BestillingsveilederContext)
	return (
		<FormikDollyFieldArray
			name="pdldata.person.forelderBarnRelasjon"
			header={'Relasjon'}
			newEntry={getInitialBarn(opts?.identtype === 'NPID' ? 'PDL' : 'FREG')}
			canBeEmpty={false}
		>
			{(path: string, idx: number) => {
				return (
					<ForelderBarnRelasjonForm formMethods={formMethods} path={path} identtype={opts?.identtype} />
				)
			}}
		</FormikDollyFieldArray>
	)
}
