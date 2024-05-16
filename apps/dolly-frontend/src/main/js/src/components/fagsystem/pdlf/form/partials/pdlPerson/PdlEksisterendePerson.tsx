import React, { useContext } from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import Loading from '@/components/ui/loading/Loading'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { Option } from '@/service/SelectOptionsOppslag'
import { ForeldreBarnRelasjon, NyIdent } from '@/components/fagsystem/pdlf/PdlTypes'
import { Alert } from '@navikt/ds-react'
import { useGruppeIdenter } from '@/utils/hooks/useGruppe'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { usePdlOptions } from '@/utils/hooks/useSelectOptions'

interface PdlEksisterendePersonValues {
	nyPersonPath?: string
	eksisterendePersonPath: string
	label: string
	formMethods: UseFormReturn
	idx?: number
	disabled?: boolean
	nyIdentValg?: NyIdent
	eksisterendeNyPerson?: Option
}

export const PdlEksisterendePerson = ({
	nyPersonPath,
	eksisterendePersonPath,
	label,
	formMethods,
	idx,
	disabled = false,
	nyIdentValg = null,
	eksisterendeNyPerson = null,
}: PdlEksisterendePersonValues) => {
	const opts: any = useContext(BestillingsveilederContext)
	const antall = opts?.antall || 1
	const gruppeId = opts?.gruppeId || opts?.gruppe?.id

	console.log('opts', opts)

	const { identer, loading: gruppeLoading, error: gruppeError } = useGruppeIdenter(gruppeId)
	const filtrerteIdenter = identer?.filter((ident) => ident.master == opts?.identMaster || 'PDLF')

	const { data: pdlOptions, loading: pdlLoading, error: pdlError } = usePdlOptions(filtrerteIdenter)

	const harSivilstand = eksisterendePersonPath?.includes('sivilstand')
	const harNyIdent = eksisterendePersonPath?.includes('nyident')

	const gyldigeSivilstanderForPartner = [
		'UOPPGITT',
		'UGIFT',
		'ENKE_ELLER_ENKEMANN',
		'SKILT',
		'SKILT_PARTNER',
		'GJENLEVENDE_PARTNER',
	]

	const harForeldreansvarForValgteBarn = (foreldreansvar: Array<string>) => {
		let harEksisterendeAnsvar = false
		const valgteBarn = formMethods
			.watch('pdldata.person.forelderBarnRelasjon')
			?.filter((relasjon: ForeldreBarnRelasjon) => relasjon.relatertPersonsRolle === 'BARN')
			?.map((relasjon: ForeldreBarnRelasjon) => relasjon.relatertPerson)

		valgteBarn?.forEach((barn: string) => {
			if (foreldreansvar.includes(barn)) {
				harEksisterendeAnsvar = true
			}
		})
		return harEksisterendeAnsvar
	}

	const getAntallForeldre = (eksisterendeForeldre: Array<string>) => {
		const partnerErForelder = () =>
			formMethods
				.watch('pdldata.person.sivilstand')
				?.find(
					(partner) => partner.type && !gyldigeSivilstanderForPartner.includes(partner.type),
				) && !formMethods.watch(`pdldata.person.forelderBarnRelasjon[${idx}].partnerErIkkeForelder`)
		const antallEksisterendeForeldre = eksisterendeForeldre.length
		const antallNyeForeldre = parseInt(antall) + (partnerErForelder() ? parseInt(antall) : 0)
		return antallEksisterendeForeldre + antallNyeForeldre
	}

	const filterOptions = (person: Option) => {
		if (harSivilstand) {
			return gyldigeSivilstanderForPartner.includes(person.sivilstand)
		} else if (eksisterendePersonPath?.includes('vergemaal')) {
			return !person.vergemaal && person.alder > 17
		} else if (
			eksisterendePersonPath?.includes('fullmakt') ||
			eksisterendePersonPath?.includes('kontaktinformasjonForDoedsbo')
		) {
			return person.alder > 17
		} else if (
			eksisterendePersonPath?.includes('forelderBarnRelasjon') &&
			formMethods.watch(`pdldata.person.forelderBarnRelasjon[${idx}].relatertPersonsRolle`) ===
				'BARN'
		) {
			return (
				getAntallForeldre(person.foreldre) < 3 &&
				formMethods
					.watch('pdldata.person.forelderBarnRelasjon')
					.some(
						(relasjon: ForeldreBarnRelasjon, relasjonId: number) =>
							relasjon.relatertPerson === person.value && relasjonId !== idx,
					)
			)
		} else if (eksisterendePersonPath?.includes('foreldreansvar')) {
			return (
				!harForeldreansvarForValgteBarn(person.foreldreansvar) &&
				!person.doedsfall &&
				person.alder > 17
			)
		}
		return true
	}

	const getFilteredOptionList = (opts) => {
		const eksisterendeIdent = opts?.personFoerLeggTil?.pdlforvalter?.person?.ident
		const tmpOptions = pdlOptions?.filter(
			(person) => person.value !== eksisterendeIdent && filterOptions(person),
		)
		if (
			eksisterendeNyPerson &&
			!tmpOptions.find((person) => person.value === eksisterendeNyPerson.value)
		) {
			tmpOptions.push(eksisterendeNyPerson)
		}
		return tmpOptions
	}

	const hasNyPersonValues = nyIdentValg
		? !isEmpty(nyIdentValg, ['syntetisk'])
		: nyPersonPath && !isEmpty(formMethods.watch(nyPersonPath), ['syntetisk'])

	const bestillingFlerePersoner = parseInt(antall) > 1 && (harSivilstand || harNyIdent)

	const filteredOptions = getFilteredOptionList(opts)

	return (
		<div className={'flexbox--full-width'}>
			{(pdlLoading || gruppeLoading) && <Loading label="Henter valg for eksisterende ident..." />}
			{filteredOptions?.length > 0 ? (
				<FormSelect
					name={eksisterendePersonPath}
					onChange={(person) => {
						formMethods.setValue(eksisterendePersonPath, person?.value || null)
						formMethods.trigger('pdldata.person.kontaktinformasjonForDoedsbo')
					}}
					label={label}
					options={filteredOptions}
					size={'xlarge'}
					isDisabled={hasNyPersonValues || bestillingFlerePersoner || disabled}
				/>
			) : pdlError || gruppeError ? (
				<Alert variant="error" size="small" style={{ marginBottom: '15px' }}>
					{pdlError?.message || gruppeError?.message || 'Feil ved henting av personer'}
				</Alert>
			) : (
				!pdlLoading &&
				!gruppeLoading && (
					<Alert variant="info" size="small" style={{ marginBottom: '15px' }}>
						Det finnes ingen eksisterende gyldige personer i denne gruppen.
					</Alert>
				)
			)}
		</div>
	)
}
