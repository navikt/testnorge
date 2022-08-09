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
import { NyIdent } from '~/components/fagsystem/pdlf/PdlTypes'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'

interface PdlEksisterendePersonValues {
	nyPersonPath?: string
	eksisterendePersonPath: string
	label: string
	formikBag?: FormikProps<{}>
	disabled?: boolean
	nyIdentValg?: NyIdent
}

export const PdlEksisterendePerson = ({
	nyPersonPath,
	eksisterendePersonPath,
	label,
	formikBag,
	disabled = false,
	nyIdentValg = null,
}: PdlEksisterendePersonValues) => {
	const opts = useContext(BestillingsveilederContext)
	const { gruppeId, antall } = opts
	const isTestnorgeIdent = identFraTestnorge(opts)

	const [identOptions, setIdentOptions] = useState<Array<Option>>([])
	const [loadingIdentOptions, setLoadingIdentOptions] = useBoolean(true)

	const gyldigeSivilstanderForPartner = [
		'UOPPGITT',
		'UGIFT',
		'ENKE_ELLER_ENKEMANN',
		'SKILT',
		'SKILT_PARTNER',
		'GJENLEVENDE_PARTNER',
	]

	useEffect(() => {
		if (!isTestnorgeIdent && gruppeId) {
			const eksisterendeIdent = opts.personFoerLeggTil?.pdlforvalter?.person?.ident
			// @ts-ignore
			SelectOptionsOppslag.hentGruppeIdentOptions(gruppeId).then((response: [Option]) => {
				setIdentOptions(
					response?.filter((person) => {
						return (
							person.value !== eksisterendeIdent &&
							(eksisterendePersonPath?.includes('sivilstand')
								? gyldigeSivilstanderForPartner.includes(person.sivilstand)
								: true)
						)
					})
				)
				setLoadingIdentOptions(false)
			})
		}
	}, [])

	const hasNyPersonValues = nyIdentValg
		? !isEmpty(nyIdentValg)
		: nyPersonPath && !isEmpty(_get(formikBag?.values, nyPersonPath))

	const bestillingFlerePersoner = parseInt(antall) > 1

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
					<AlertStripeInfo style={{ marginBottom: '15px' }}>
						Det finnes ingen eksisterende personer i denne gruppen.
					</AlertStripeInfo>
				)
			)}
		</div>
	)
}
