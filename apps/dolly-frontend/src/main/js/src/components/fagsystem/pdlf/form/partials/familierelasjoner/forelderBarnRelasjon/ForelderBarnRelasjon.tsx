import * as React from 'react'
import { useContext, useEffect, useState } from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import {
	getInitialBarn,
	getInitialForelder,
	initialPdlBiPerson,
	initialPdlPerson,
} from '@/components/fagsystem/pdlf/form/initialValues'
import * as _ from 'lodash-es'
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
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import StyledAlert from '@/components/ui/alert/StyledAlert'

interface ForelderForm {
	formMethods: UseFormReturn
	path?: string
	idx?: number
	eksisterendeNyPerson?: any
	identtype?: string
	ident?: string
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
	idx,
	eksisterendeNyPerson = null,
	identtype,
	ident,
}: ForelderForm) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	const antall = opts?.antall || 1
	const identMaster = opts?.identMaster || 'PDLF'

	const [erBarn, setErBarn] = useState(
		formMethods.watch(`${path}.relatertPersonsRolle`) === RELASJON_BARN,
	)

	const relatertPerson = 'relatertPerson'
	const nyRelatertPerson = 'nyRelatertPerson'
	const relatertPersonUtenFolkeregisteridentifikator =
		'relatertPersonUtenFolkeregisteridentifikator'

	const getRolle = () => {
		const rolle = formMethods.watch(`${path}.relatertPersonsRolle`)
		return forelderTyper.includes(rolle) ? RELASJON_FORELDER : RELASJON_BARN
	}

	const [relatertPersonsRolle, setRelatertPersonsRolle] = useState(getRolle())

	useEffect(() => {
		setRelatertPersonsRolle(getRolle())
		setErBarn(formMethods.watch(`${path}.relatertPersonsRolle`) === RELASJON_BARN)
	}, [formMethods.watch('pdldata.person.forelderBarnRelasjon')?.length])

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

	const testnorgePerson = identMaster === 'PDL'
	const initiellMaster = testnorgePerson || identtype === 'NPID' ? 'PDL' : 'FREG'
	const kanVelgeMaster = !testnorgePerson && identtype !== 'NPID'

	const typeAnsvarlig =
		antall > 1
			? Options('typeAnsvarlig').filter((value) => value.value !== 'EKSISTERENDE')
			: Options('typeAnsvarlig')

	return (
		<div className="flexbox--flex-wrap">
			<div className="toggle--wrapper">
				<ToggleGroup
					onChange={(value: string) => {
						setRelatertPersonsRolle(value)
						formMethods.setValue(
							path,
							value === RELASJON_BARN
								? { ...getInitialBarn(initiellMaster), id: id }
								: { ...getInitialForelder(initiellMaster), id: id },
						)
						setErBarn(value === RELASJON_BARN)
						formMethods.trigger(path)
					}}
					size={'small'}
					value={relatertPersonsRolle}
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
						{!testnorgePerson && (
							<FormCheckbox
								name={`${path}.borIkkeSammen`}
								id={`${path}.borIkkeSammen`}
								label="Bor ikke sammen"
								checkboxMargin
							/>
						)}
					</>
				)}
				<FormSelect
					name={`${path}.typeForelderBarn`}
					label={erBarn ? 'Type barn' : 'Type forelder'}
					options={typeAnsvarlig}
					onChange={(target: Target) => handleChangeTypeForelderBarn(target, path)}
					size="medium"
					vis={!testnorgePerson}
					info={
						opts?.antall > 1 && '"Eksisterende person" er tilgjengelig for individ, ikke for gruppe'
					}
				/>
			</div>
			{identMaster === 'PDLF' &&
				getForelderBarnType() === 'EKSISTERENDE' &&
				!ident &&
				!formMethods.getValues().pdldata?.opprettNyPerson?.alder && (
					<StyledAlert variant={'warning'} size={'small'}>
						Ved "Eksisterende person" må alder oppgis på hovedpersonen: Gå tilbake til "Velg
						egenskaper", huk av for alder og sett en verdi så aldersforskjell blir minst 18 år.
					</StyledAlert>
				)}
			{(testnorgePerson || getForelderBarnType() === TypeAnsvarlig.EKSISTERENDE) && (
				<PdlEksisterendePerson
					eksisterendePersonPath={`${path}.relatertPerson`}
					label={erBarn ? RELASJON_BARN.toUpperCase() : RELASJON_FORELDER.toUpperCase()}
					formMethods={formMethods}
					eksisterendeNyPerson={eksisterendeNyPerson}
					idx={idx}
					ident={ident}
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

			<AvansertForm path={path} kanVelgeMaster={kanVelgeMaster} />
		</div>
	)
}

export const ForelderBarnRelasjon = ({ formMethods }: ForelderForm) => {
	const { identtype, identMaster, personFoerLeggTil } = useContext(
		BestillingsveilederContext,
	) as BestillingsveilederContextType
	const initiellMaster = identMaster === 'PDL' || identtype === 'NPID' ? 'PDL' : 'FREG'

	const handleRemoveEntry = (idx: number) => {
		const forelderBarnListe = formMethods.watch('pdldata.person.forelderBarnRelasjon')
		const filterForelderBarnListe = forelderBarnListe?.filter((_, index) => index !== idx)
		formMethods.setValue('pdldata.person.forelderBarnRelasjon', filterForelderBarnListe)
		formMethods.trigger('pdldata.person.forelderBarnRelasjon')
	}

	return (
		<FormDollyFieldArray
			name="pdldata.person.forelderBarnRelasjon"
			header={'Relasjon'}
			newEntry={getInitialBarn(initiellMaster)}
			canBeEmpty={false}
			handleRemoveEntry={handleRemoveEntry}
		>
			{(path: string, idx: number) => {
				return (
					<ForelderBarnRelasjonForm
						formMethods={formMethods}
						path={path}
						idx={idx}
						ident={personFoerLeggTil?.pdl?.ident}
					/>
				)
			}}
		</FormDollyFieldArray>
	)
}
