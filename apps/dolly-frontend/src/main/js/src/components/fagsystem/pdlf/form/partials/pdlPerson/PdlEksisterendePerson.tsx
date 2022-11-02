import React, { useContext, useEffect, useState } from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import Loading from '~/components/ui/loading/Loading'
import { isEmpty } from '~/components/fagsystem/pdlf/form/partials/utils'
import _get from 'lodash/get'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { identFraTestnorge } from '~/components/bestillingsveileder/stegVelger/steg/steg1/Steg1Person'
import { Option, SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { useBoolean } from 'react-use'
import { FormikProps } from 'formik'
import { ForeldreBarnRelasjon, NyIdent, Sivilstand } from '~/components/fagsystem/pdlf/PdlTypes'
import { Alert } from '@navikt/ds-react'
import { useParams } from 'react-router-dom'

interface PdlEksisterendePersonValues {
	nyPersonPath?: string
	eksisterendePersonPath: string
	label: string
	formikBag?: FormikProps<{}>
	idx?: number
	disabled?: boolean
	nyIdentValg?: NyIdent
	eksisterendeNyPerson?: Option
}

export const PdlEksisterendePerson = ({
	nyPersonPath,
	eksisterendePersonPath,
	label,
	formikBag,
	idx,
	disabled = false,
	nyIdentValg = null,
	eksisterendeNyPerson = null,
}: PdlEksisterendePersonValues) => {
	const opts = useContext(BestillingsveilederContext)
	const antall = opts?.antall || 1
	const { gruppeId } = useParams()

	const isTestnorgeIdent = identFraTestnorge(opts)

	const [identOptions, setIdentOptions] = useState<Array<Option>>([])
	const [loadingIdentOptions, setLoadingIdentOptions] = useBoolean(true)

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
		const valgteBarn = _get(formikBag?.values, 'pdldata.person.forelderBarnRelasjon')
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
			_get(formikBag?.values, 'pdldata.person.sivilstand')?.find(
				(partner: Sivilstand) =>
					partner.type && !gyldigeSivilstanderForPartner.includes(partner.type)
			) &&
			!_get(formikBag?.values, `pdldata.person.forelderBarnRelasjon[${idx}].partnerErIkkeForelder`)
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
			_get(
				formikBag?.values,
				`pdldata.person.forelderBarnRelasjon[${idx}].relatertPersonsRolle`
			) === 'BARN'
		) {
			return (
				getAntallForeldre(person.foreldre) < 3 &&
				!_get(formikBag.values, 'pdldata.person.forelderBarnRelasjon').some(
					(relasjon: ForeldreBarnRelasjon, relasjonId: number) =>
						relasjon.relatertPerson === person.value && relasjonId !== idx
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

	const getFilteredOptionList = () => {
		const eksisterendeIdent = opts?.personFoerLeggTil?.pdlforvalter?.person?.ident
		let tmpOptions = []
		// @ts-ignore
		SelectOptionsOppslag.hentGruppeIdentOptions(gruppeId).then((response: [Option]) => {
			tmpOptions = response?.filter((person) => {
				return person.value !== eksisterendeIdent && filterOptions(person)
			})
			if (
				eksisterendeNyPerson &&
				!tmpOptions.find((person) => person.value === eksisterendeNyPerson.value)
			) {
				tmpOptions.push(eksisterendeNyPerson)
			}
			setIdentOptions(tmpOptions)
			setLoadingIdentOptions(false)
		})
	}

	useEffect(() => {
		if (!isTestnorgeIdent && gruppeId) {
			getFilteredOptionList()
		}
	}, [])

	useEffect(() => {
		if (formikBag) {
			getFilteredOptionList()
		}
	}, [formikBag])

	const hasNyPersonValues = nyIdentValg
		? !isEmpty(nyIdentValg, ['syntetisk'])
		: nyPersonPath && !isEmpty(_get(formikBag?.values, nyPersonPath), ['syntetisk'])

	const bestillingFlerePersoner = parseInt(antall) > 1 && (harSivilstand || harNyIdent)

	return (
		<div className={'flexbox--full-width'}>
			{loadingIdentOptions && <Loading label="Henter valg for eksisterende ident..." />}
			{identOptions?.length > 0 ? (
				<FormikSelect
					name={eksisterendePersonPath}
					label={label}
					options={identOptions}
					size={'xlarge'}
					disabled={hasNyPersonValues || bestillingFlerePersoner || disabled}
				/>
			) : (
				!loadingIdentOptions && (
					<Alert variant="info" size="small" style={{ marginBottom: '15px' }}>
						Det finnes ingen eksisterende gyldige personer i denne gruppen.
					</Alert>
				)
			)}
		</div>
	)
}
