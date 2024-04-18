import * as React from 'react'
import { useContext, useEffect } from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	getInitialBarn,
	getInitialForelder,
	initialPdlBiPerson,
	initialPdlPerson,
} from '@/components/fagsystem/pdlf/form/initialValues'
import _ from 'lodash'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { BarnRelasjon } from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/forelderBarnRelasjon/BarnRelasjon'
import { TypeAnsvarlig } from '@/components/fagsystem/pdlf/PdlTypes'
import { PdlEksisterendePerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlEksisterendePerson'
import { PdlPersonUtenIdentifikator } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonUtenIdentifikator'
import { PdlNyPerson } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlNyPerson'
import { Alert, ToggleGroup } from '@navikt/ds-react'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'

interface ForelderForm {
	formMethods: UseFormReturn
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
	formMethods,
	path,
	eksisterendeNyPerson = null,
	identtype,
}: ForelderForm) => {
	const [erBarn, setErBarn] = React.useState(
		formMethods.watch(`${path}.relatertPersonsRolle`) === RELASJON_BARN,
	)
	const relatertPerson = 'relatertPerson'
	const nyRelatertPerson = 'nyRelatertPerson'
	const relatertPersonUtenFolkeregisteridentifikator =
		'relatertPersonUtenFolkeregisteridentifikator'

	const handleChangeTypeForelderBarn = (target: Target, path: string) => {
		const forelderBarnRelasjon = formMethods.watch(path)
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
		formMethods.trigger(path)
	}

	const relatertPersonsRolle = forelderTyper.includes(
		formMethods.watch(`${path}.relatertPersonsRolle`),
	)
		? RELASJON_FORELDER
		: RELASJON_BARN

	const id = formMethods.watch(`${path}.id`)

	const getForelderBarnType = () => {
		const forelderBarnType = formMethods.watch(`${path}.typeForelderBarn`)
		if (forelderBarnType) {
			return forelderBarnType
		} else if (formMethods.watch(`${path}.relatertPerson`)) {
			return 'EKSISTERENDE'
		} else if (formMethods.watch(`${path}.relatertPersonUtenFolkeregisteridentifikator`)) {
			return 'UTEN_ID'
		} else return null
	}

	useEffect(() => {
		if (!formMethods.watch(`${path}.typeForelderBarn`)) {
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
						setErBarn(value === RELASJON_BARN)
						formMethods.trigger(path)
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
						<FormSelect
							name={`${path}.relatertPersonsRolle`}
							label="Foreldretype"
							options={Options('foreldreTypePDL')}
							isClearable={false}
						/>
						<FormCheckbox name={`${path}.borIkkeSammen`} label="Bor ikke sammen" checkboxMargin />
					</>
				)}
				<FormSelect
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

			{!path?.includes('pdldata') && erBarn && formMethods.watch('harDeltBosted') && (
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

			{!path?.includes('pdldata') && formMethods.watch('harForeldreansvar') && (
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
		<FormDollyFieldArray
			name="pdldata.person.forelderBarnRelasjon"
			header={'Relasjon'}
			newEntry={getInitialBarn(opts?.identtype === 'NPID' ? 'PDL' : 'FREG')}
			canBeEmpty={false}
		>
			{(path: string, idx: number) => {
				return (
					<ForelderBarnRelasjonForm
						formMethods={formMethods}
						path={path}
						identtype={opts?.identtype}
					/>
				)
			}}
		</FormDollyFieldArray>
	)
}
