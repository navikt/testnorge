import React, { useContext, useEffect, useState } from 'react'
import Panel from '~/components/ui/panel/Panel'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { erForste, panelError } from '~/components/ui/form/formUtils'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { Sivilstand } from '~/components/fagsystem/pdlf/form/partials/sivilstand/Sivilstand'
import { Option, SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import { FormikProps } from 'formik'
import { DoedfoedtBarn } from '~/components/fagsystem/pdlf/form/partials/doedfoedtBarn/DoedfoedtBarn'
import { ForelderBarnRelasjon } from '~/components/fagsystem/pdlf/form/partials/forelderBarnRelasjon/ForelderBarnRelasjon'

const infoTekst =
	'Savner du noen egenskaper for partner/barn? ' +
	'Du kan nå opprette personene hver for seg (uten relasjoner) og koble dem sammen etter de er bestilt. ' +
	'På denne måten kan partner og barn få flere typer egenskaper. ' +
	'Hvis du vil legge inn familierelasjoner raskt gjør du dette her.'

const relasjonerAttributter = [
	'tpsf.relasjoner',
	'pdldata.person.sivilstand',
	'pdldata.person.forelderBarnRelasjon',
	'pdldata.person.doedfoedtBarn',
]

export const Familierelasjoner = ({ formikBag }: { formikBag: FormikProps<any> }) => {
	const opts = useContext(BestillingsveilederContext)
	const { gruppeId } = opts

	const [identOptions, setIdentOptions] = useState<Array<Option>>([])
	useEffect(() => {
		SelectOptionsOppslag.hentGruppeIdentOptions(gruppeId).then((response: [Option]) =>
			setIdentOptions(response)
		)
	}, [])

	return (
		<Vis attributt={relasjonerAttributter}>
			<Panel
				heading="Familierelasjoner"
				informasjonstekst={infoTekst}
				hasErrors={panelError(formikBag, 'pdldata.person')}
				iconType={'relasjoner'}
				// @ts-ignore
				startOpen={() => erForste(formikBag.values, [relasjonerAttributter])}
				checkAttributeArray={undefined}
				uncheckAttributeArray={undefined}
			>
				<Kategori title="Sivilstand (partner)" vis="pdldata.person.sivilstand">
					<Sivilstand formikBag={formikBag} identOptions={identOptions} />
				</Kategori>
				<Kategori title="Barn/Foreldre" vis="pdldata.person.forelderBarnRelasjon">
					<ForelderBarnRelasjon formikBag={formikBag} identOptions={identOptions} />
				</Kategori>
				<Kategori title="Dødfødt barn" vis="pdldata.person.doedfoedtBarn">
					<DoedfoedtBarn formikBag={formikBag} />
				</Kategori>
			</Panel>
		</Vis>
	)
}
