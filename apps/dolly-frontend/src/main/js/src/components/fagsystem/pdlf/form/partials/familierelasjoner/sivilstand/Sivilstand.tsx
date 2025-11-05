import * as React from 'react'
import { useContext } from 'react'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { PdlPersonExpander } from '@/components/fagsystem/pdlf/form/partials/pdlPerson/PdlPersonExpander'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import {
	getInitialSivilstand,
	initialPdlPerson,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { Option } from '@/service/SelectOptionsOppslag'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { UseFormReturn } from 'react-hook-form/dist/types'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import { isAfter } from 'date-fns'
import { formatDate } from '@/utils/DataFormatter'

interface SivilstandFormTypes {
	formMethods: UseFormReturn
	path?: string
	eksisterendeNyPerson?: Option | null
	identtype?: string
	ident?: string
}

export const gyldigeSivilstander = [
	'GIFT',
	'REGISTRERT_PARTNER',
	'SEPARERT',
	'SEPARERT_PARTNER',
	'SAMBOER',
]

export const SivilstandForm = ({
	path,
	formMethods,
	eksisterendeNyPerson = null,
	identtype,
	ident,
}: SivilstandFormTypes) => {
	const handleTypeChange = (selected: any, path: string) => {
		formMethods.setValue(`${path}.type`, selected.value)
		if (!gyldigeSivilstander.includes(selected.value)) {
			formMethods.setValue(`${path}.borIkkeSammen`, false)
			formMethods.setValue(`${path}.relatertVedSivilstand`, null)
			formMethods.setValue(`${path}.nyRelatertPerson`, initialPdlPerson)
		}
		if (selected.value === 'SAMBOER') {
			formMethods.setValue(`${path}.bekreftelsesdato`, null)
		}
		formMethods.trigger(path)
	}

	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const identMaster = opts?.identMaster || (parseInt(ident?.charAt(2)) >= 8 ? 'PDL' : 'PDLF')

	const isTestnorgeIdent = identMaster === 'PDL'
	const kanVelgeMaster = !isTestnorgeIdent && (opts?.identtype !== 'NPID' || identtype !== 'NPID')

	const kanHaRelatertPerson = gyldigeSivilstander.includes(formMethods.watch(`${path}.type`))

	return (
		<div className="flexbox--flex-wrap sivilstand-form">
			<FormSelect
				name={`${path}.type`}
				label="Type sivilstand"
				options={Options('sivilstandType')}
				onChange={(selected: any) => handleTypeChange(selected, path)}
				isClearable={false}
			/>
			{formMethods.watch(`${path}.type`) === 'SAMBOER' && (
				<div style={{ marginLeft: '-20px', marginRight: '20px', paddingTop: '27px' }}>
					<Hjelpetekst>
						Samboer eksisterer ikke i PDL. Personer med denne typen sisvilstand vil derfor vises som
						ugift i fagsystemene.
					</Hjelpetekst>
				</div>
			)}
			<FormDatepicker
				name={`${path}.sivilstandsdato`}
				label="Gyldig fra og med"
				disabled={formMethods.watch(`${path}.bekreftelsesdato`) != null}
			/>
			<FormDatepicker
				name={`${path}.bekreftelsesdato`}
				label="Bekreftelsesdato"
				disabled={
					formMethods.watch(`${path}.sivilstandsdato`) != null ||
					formMethods.watch(`${path}.master`) !== 'PDL' ||
					formMethods.watch(`${path}.type`) === 'SAMBOER'
				}
			/>
			<FormCheckbox
				name={`${path}.borIkkeSammen`}
				id={`${path}.borIkkeSammen`}
				label="Bor ikke sammen"
				disabled={!kanHaRelatertPerson}
				vis={!isTestnorgeIdent}
				checkboxMargin
			/>
			{kanHaRelatertPerson && (
				<PdlPersonExpander
					path={path}
					nyPersonPath={`${path}.nyRelatertPerson`}
					eksisterendePersonPath={`${path}.relatertVedSivilstand`}
					eksisterendeNyPerson={eksisterendeNyPerson}
					label={'PERSON RELATERT TIL'}
					formMethods={formMethods}
					isExpanded={
						isTestnorgeIdent ||
						!isEmpty(formMethods.watch(`${path}.nyRelatertPerson`), ['syntetisk']) ||
						formMethods.watch(`${path}.relatertVedSivilstand`) !== null
					}
				/>
			)}
			<AvansertForm
				path={path}
				kanVelgeMaster={formMethods.watch(`${path}.bekreftelsesdato`) === null && kanVelgeMaster}
			/>
		</div>
	)
}

export const Sivilstand = ({ formMethods }: SivilstandFormTypes) => {
	// @ts-ignore
	const { identtype, identMaster, personFoerLeggTil } = useContext(BestillingsveilederContext)
	const initiellMaster = identMaster === 'PDL' || identtype === 'NPID' ? 'PDL' : 'FREG'

	const sivilstandListe = formMethods.watch('pdldata.person.sivilstand')

	const getAlderspensjonAlert = () => {
		const harAlderspensjon = personFoerLeggTil?.alderspensjon?.length > 0
		const harSivilstanddato = sivilstandListe?.some(
			(sivilstand) => sivilstand.sivilstandsdato || sivilstand.bekreftelsesdato,
		)
		if (harAlderspensjon && !harSivilstanddato) {
			return 'Personen har registrert alderspensjon. For automatisk revurderingsvedtak må dato for endring av sivilstand settes.'
		}
		const alderspensjonIverksettelsesdato = personFoerLeggTil?.alderspensjon?.find(
			(ap) => ap?.data?.transaksjonId?.iverksettelsesdato,
		)?.data?.transaksjonId?.iverksettelsesdato
		const harGyldigSivilstanddato = sivilstandListe?.some((sivilstand) =>
			isAfter(new Date(sivilstand.sivilstandsdato), new Date(alderspensjonIverksettelsesdato)),
		)
		if (harAlderspensjon && harSivilstanddato && !harGyldigSivilstanddato) {
			return `Personen har registrert alderspensjon. For automatisk revurderingsvedtak må dato for endring av sivilstand settes til etter ${formatDate(alderspensjonIverksettelsesdato)}`
		}
		return null
	}
	const alderspensjonAlert = getAlderspensjonAlert()

	const handleRemoveEntry = (idx: number) => {
		const filterSivilstandListe = sivilstandListe?.filter((_, index) => index !== idx)
		formMethods.setValue('pdldata.person.sivilstand', filterSivilstandListe)
		formMethods.trigger('pdldata.person.sivilstand')
	}

	return (
		<>
			{alderspensjonAlert && (
				<StyledAlert variant={'info'} size={'small'}>
					{alderspensjonAlert}
				</StyledAlert>
			)}
			<FormDollyFieldArray
				name="pdldata.person.sivilstand"
				header="Sivilstand"
				newEntry={getInitialSivilstand(initiellMaster)}
				canBeEmpty={false}
				handleRemoveEntry={handleRemoveEntry}
			>
				{(path: string) => <SivilstandForm path={path} formMethods={formMethods} />}
			</FormDollyFieldArray>
		</>
	)
}
