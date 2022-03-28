import React from 'react'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { HentPerson } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { PdlForeldreBarn } from '~/components/fagsystem/pdl/visning/partials/relasjoner/PdlForeldreBarn'
import { PdlPartner } from '~/components/fagsystem/pdl/visning/partials/relasjoner/PdlPartner'
import { Sivilstand } from '~/components/fagsystem/pdlf/PdlTypes'
import { PdlForeldreansvar } from '~/components/fagsystem/pdl/visning/partials/relasjoner/PdlForeldreansvar'
import { PdlDoedfoedtBarn } from '~/components/fagsystem/pdl/visning/partials/relasjoner/PdlDoedfoedtBarn'

type PdlRelasjonerProps = {
	data: HentPerson
	visTittel?: boolean
}

export const PdlRelasjoner = ({ data }: PdlRelasjonerProps) => {
	if (!data) {
		return null
	}

	const partnere = data.sivilstand?.filter((sivilstand: Sivilstand) => sivilstand.type !== 'UGIFT')
	const doedfoedtBarn = data.doedfoedtBarn
	const foreldreBarn = data.forelderBarnRelasjon
	const foreldreansvar = data.foreldreansvar

	if (hasNoValues(partnere) && hasNoValues(doedfoedtBarn) && hasNoValues(foreldreBarn)) return null

	return (
		<ErrorBoundary>
			<>
				<PdlPartner data={partnere} />
				<PdlDoedfoedtBarn data={doedfoedtBarn} />
				<PdlForeldreBarn data={foreldreBarn} />
				<PdlForeldreansvar data={foreldreansvar} />
			</>
		</ErrorBoundary>
	)
}

export const hasNoValues = (liste: any[]) => {
	return !liste || liste.length === 0
}
