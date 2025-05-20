import React, { useContext } from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import Loading from '@/components/ui/loading/Loading'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { Option } from '@/service/SelectOptionsOppslag'
import { ForeldreBarnRelasjon } from '@/components/fagsystem/pdlf/PdlTypes'
import { Alert } from '@navikt/ds-react'
import { useGruppeIdenter } from '@/utils/hooks/useGruppe'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { usePdlOptions } from '@/utils/hooks/useSelectOptions'

interface PdlEksisterendePersonValues {
	eksisterendePersonPath: string
	fullmektigsNavnPath?: string
	label: string
	formMethods: UseFormReturn
	idx?: number
	disabled?: boolean
	eksisterendeNyPerson?: Option
	ident?: string
}

export const PdlEksisterendePerson = ({
	eksisterendePersonPath,
	label,
	formMethods,
	idx,
	disabled = false,
	eksisterendeNyPerson = null as unknown as Option,
	fullmektigsNavnPath = null as unknown as string,
	path = null as unknown as string,
	ident,
}: PdlEksisterendePersonValues) => {
	const opts: any = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const formGruppeId = formMethods.watch('gruppeId')

	const antall = opts?.antall || 1
	const gruppeId =
		formGruppeId || opts?.gruppeId || opts?.gruppe?.id || window.location.pathname.split('/')?.[2]

	const { identer, loading: gruppeLoading, error: gruppeError } = useGruppeIdenter(gruppeId)
	// console.log('identer: ', identer) //TODO - SLETT MEG
	const identNy = opts?.personFoerLeggTil?.pdl?.ident || opts?.importPersoner?.[0].ident || ident
	const identMaster = opts?.identMaster || 'PDLF'

	const attributtMaster = formMethods.watch(`${path}.master`)
	// console.log('attributtMaster: ', attributtMaster) //TODO - SLETT MEG

	identer?.push({ ident: identNy, master: identMaster })

	const filtrerteIdenter = identer?.filter((ident) => ident.master == identMaster)

	const {
		data: pdlOptions,
		loading: pdlLoading,
		error: pdlError,
	} = usePdlOptions(filtrerteIdenter, identMaster, true)

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

	const checkForeldre = () => {
		const relasjoner =
			formMethods.getValues()?.pdldata?.person?.forelderBarnRelasjon ||
			Array.from(formMethods.getValues()?.forelderBarnRelasjon)

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

	const eksisterendeIdent =
		opts?.personFoerLeggTil?.pdl?.ident || opts?.importPersoner?.[0]?.ident || ident

	const eksisterendePerson = pdlOptions.find((x) => x.value === eksisterendeIdent) || {
		alder: parseInt(formMethods.getValues()?.pdldata?.opprettNyPerson?.alder),
	}

	console.log('opts: ', opts) //TODO - SLETT MEG
	//TODO: Finn eksisterende relasjoner, for aa finne ut hvilke personer som kan relateres paa nytt med annen master

	const filterOptions = (person: Option) => {
		console.log('person: ', person) //TODO - SLETT MEG
		// console.log('person?.sivilstand?.master: ', person?.sivilstand?.master) //TODO - SLETT MEG
		console.log('attributtMaster: ', attributtMaster) //TODO - SLETT MEG
		if (person.doedsfall) {
			return false
		}
		if (label === 'PERSON RELATERT TIL') {
			// Sivilstand gift/samboer osv
			//TODO: Maa sjekke alle gyldige sivilstander.
			return (
				person.alder > 17 &&
				(gyldigeSivilstanderForPartner.includes(person?.sivilstand?.type) ||
					attributtMaster !== person?.sivilstand?.master)
			)
		} else if (label === 'FULLMEKTIG' || label === 'Kontaktperson') {
			return person.alder > 17
		} else if (label === 'VERGE') {
			return person.alder > 17 && !person.vergemaal
			// return person.alder > 17 && (!person.vergemaal || person.vergemaal === 'VERGE') //TODO: Maa skrives riktig
		} else if (label === 'BARN') {
			// eksisterende person er forelder
			return eksisterendePerson?.alder - person.alder > 17 && getAntallForeldre(person.foreldre) < 3
		} else if (label === 'FORELDER') {
			// eksisterende person er barn
			return person.alder - eksisterendePerson?.alder > 17 && checkForeldre()
		} else if (label === 'Ansvarlig') {
			return person.alder > 17 && !harForeldreansvarForValgteBarn(person.foreldreansvar)
		} else if (label.toUpperCase().includes('ANSVARSSUBJEKT')) {
			return person.alder < 18
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

	const bestillingFlerePersoner = parseInt(antall) > 1 && (harSivilstand || harNyIdent)

	const filteredOptions = getFilteredOptionList()

	return (
		<div className={'flexbox--full-width'}>
			{(pdlLoading || gruppeLoading) && <Loading label="Henter valg for eksisterende ident..." />}
			{filteredOptions?.length > 0 ? (
				<FormSelect
					name={eksisterendePersonPath}
					onChange={(person) => {
						const navn = person?.label?.match(/-\s(.*?)\s\(/)
						formMethods.setValue(eksisterendePersonPath, person?.value || null)
						if (fullmektigsNavnPath) {
							formMethods.setValue(fullmektigsNavnPath, navn?.[1] || person?.label || null)
						}
						formMethods.trigger('pdldata.person')
					}}
					label={label}
					options={filteredOptions}
					size={'xxlarge'}
					isDisabled={bestillingFlerePersoner || disabled}
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
