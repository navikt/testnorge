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

interface PdlEksisterendePersonValues {
	nyPersonPath?: string
	eksisterendePersonPath: string
	label: string
	formikBag?: FormikProps<{}>
	disabled?: boolean
}

export const PdlEksisterendePerson = ({
	nyPersonPath,
	eksisterendePersonPath,
	label,
	formikBag,
	disabled = false,
}: PdlEksisterendePersonValues) => {
	const opts = useContext(BestillingsveilederContext)
	const { gruppeId } = opts
	const isTestnorgeIdent = identFraTestnorge(opts)

	const [identOptions, setIdentOptions] = useState<Array<Option>>([])
	const [loadingIdentOptions, setLoadingIdentOptions] = useBoolean(true)

	useEffect(() => {
		if (!isTestnorgeIdent && gruppeId) {
			const eksisterendeIdent = opts.personFoerLeggTil?.pdlforvalter?.person?.ident
			SelectOptionsOppslag.hentGruppeIdentOptions(gruppeId).then((response: [Option]) => {
				setIdentOptions(response?.filter((person) => person.value !== eksisterendeIdent))
				setLoadingIdentOptions(false)
			})
		}
	}, [])

	const hasNyPersonValues = nyPersonPath && !isEmpty(_get(formikBag?.values, nyPersonPath))

	return (
		<>
			{loadingIdentOptions && <Loading label="Henter valg for eksisterende ident..." />}
			{identOptions?.length > 0 && (
				<FormikSelect
					name={eksisterendePersonPath}
					label={label}
					options={identOptions}
					size={'xlarge'}
					disabled={hasNyPersonValues || disabled}
				/>
			)}
		</>
	)
}
