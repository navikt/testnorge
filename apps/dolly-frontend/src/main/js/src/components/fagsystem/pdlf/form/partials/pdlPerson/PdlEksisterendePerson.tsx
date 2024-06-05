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
	idx: number
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
	const opts = useContext(BestillingsveilederContext)

	const antall = opts?.antall || 1
	const gruppeId = opts?.gruppeId || opts?.gruppe?.id

	const { identer, loading: gruppeLoading, error: gruppeError } = useGruppeIdenter(gruppeId)
	const identNy = opts?.personFoerLeggTil?.pdl?.ident || opts?.importPersoner?.[0].ident
	const identMaster = opts?.identMaster || 'PDLF'

	identer?.push({ ident: identNy, master: identMaster })

	const filtrerteIdenter = identer?.filter((ident) => ident.master == identMaster)

	const {
		data: pdlOptions,
		loading: pdlLoading,
		error: pdlError,
	} = usePdlOptions(filtrerteIdenter, identMaster)

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
		const antallEksisterendeForeldre = eksisterendeForeldre?.length
		const antallNyeForeldre = parseInt(antall) + (partnerErForelder() ? parseInt(antall) : 0)
		return antallEksisterendeForeldre + antallNyeForeldre
	}

	const checkForeldre = (person: object) => {
		const relasjoner = formMethods.getValues()?.pdldata?.person?.forelderBarnRelasjon
		if (!!relasjoner.find((relasjon) => relasjon.relatertPerson === person.value)) {
			return false
		}

		let antallForeldre = eksisterendePerson.foreldre?.length || 0
		for (let i = 0; i < relasjoner.length; i++) {
			if (relasjoner[i].minRolleForPerson === 'BARN') {
				antallForeldre++
			}
			if (idx === i) {
				return antallForeldre < 3
			}
		}
		return true
	}

	const eksisterendeIdent = opts?.personFoerLeggTil?.pdl?.ident || opts?.importPersoner?.[0]?.ident

	const eksisterendePerson = pdlOptions.find((x) => x.value === eksisterendeIdent) || {
		alder: parseInt(formMethods.getValues()?.pdldata?.opprettNyPerson?.alder),
	}

	const filterOptions = (person: Option) => {
		if (person.doedsfall) {
			return false
		}

		if (label === 'PERSON RELATERT TIL') {
			// Sivilstand gift/samboer osv
			return person.alder > 17 && gyldigeSivilstanderForPartner.includes(person?.sivilstand)
		} else if (label === 'FULLMEKTIG' || label === 'Kontaktperson') {
			return person.alder > 17
		} else if (label === 'VERGE') {
			return !person.vergemaal && person.alder > 17
		} else if (label === 'BARN') {
			// eksisterende person er forelder
			return eksisterendePerson?.alder - person.alder > 17 && getAntallForeldre(person.foreldre) < 3
		} else if (label === 'FORELDER') {
			// eksisterende person er barn
			return person.alder - eksisterendePerson?.alder > 17 && checkForeldre(person)
		} else if (label === 'Ansvarlig') {
			return person.alder > 17 && !harForeldreansvarForValgteBarn(person.foreldreansvar)
		}
		return true
	}

	const getFilteredOptionList = () => {
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

	const filteredOptions = getFilteredOptionList()

	return (
		<div className={'flexbox--full-width'}>
			{(pdlLoading || gruppeLoading) && <Loading label="Henter valg for eksisterende ident..." />}
			{filteredOptions?.length > 0 ? (
				<FormSelect
					name={eksisterendePersonPath}
					onChange={(person) => {
						formMethods.setValue(eksisterendePersonPath, person?.value || null)
						formMethods.trigger('pdldata.person')
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
