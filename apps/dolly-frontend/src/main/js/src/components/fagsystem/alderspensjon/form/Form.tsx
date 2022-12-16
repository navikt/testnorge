import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { erForsteEllerTest, panelError } from '~/components/ui/form/formUtils'
import Panel from '~/components/ui/panel/Panel'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import React, { useContext } from 'react'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { Alert } from '@navikt/ds-react'
import styled from 'styled-components'
import _has from 'lodash/has'
import _get from 'lodash/get'
import { add, addMonths, isDate, setDate } from 'date-fns'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { validation } from '~/components/fagsystem/alderspensjon/form/validation'
import { Monthpicker } from '~/components/ui/form/inputs/monthpicker/Monthpicker'
import { getAlder } from '~/ducks/fagsystem'

const StyledAlert = styled(Alert)`
	margin-bottom: 20px;
	&&& {
		.navds-alert__wrapper {
			max-width: 60rem;
		}
	}
`

const alderspensjonPath = 'pensjonforvalter.alderspensjon'

export const AlderspensjonForm = ({ formikBag }) => {
	const opts = useContext(BestillingsveilederContext)
	const { nyBestilling, leggTil, importTestnorge } = opts?.is

	const harAlder =
		_has(formikBag.values, 'pdldata.opprettNyPerson.alder') &&
		_has(formikBag.values, 'pdldata.opprettNyPerson.foedtFoer')
	const alderNyPerson = _get(formikBag.values, 'pdldata.opprettNyPerson.alder')
	const foedtFoer = _get(formikBag.values, 'pdldata.opprettNyPerson.foedtFoer')
	const harUgyldigAlder =
		(alderNyPerson && alderNyPerson < 62) ||
		(isDate(foedtFoer) && add(foedtFoer, { years: 62 }) > new Date())

	const alderLeggTilPerson = getAlder(
		_get(opts, 'personFoerLeggTil.pdl.hentPerson.foedsel[0].foedselsdato')
	)
	const alderImportertPerson = opts?.importPersoner?.map((person) =>
		getAlder(_get(person, 'data.hentPerson.foedsel[0].foedselsdato'))
	)

	const harNorskBankkonto =
		_has(formikBag.values, 'bankkonto.norskBankkonto') ||
		_has(opts, 'personFoerLeggTil.kontoregister.aktivKonto')

	return (
		<Vis attributt={alderspensjonPath}>
			<Panel
				heading="Alderspensjon"
				hasErrors={panelError(formikBag, alderspensjonPath)}
				iconType="pensjon"
				startOpen={erForsteEllerTest(formikBag.values, [alderspensjonPath])}
			>
				{nyBestilling && (!harAlder || harUgyldigAlder) && (
					<StyledAlert variant={'warning'} size={'small'}>
						For å sikre at personen har rett på alderspensjon må alder settes til 62 år eller
						høyere.
					</StyledAlert>
				)}
				{((leggTil && alderLeggTilPerson < 62) ||
					(importTestnorge && alderImportertPerson?.some((alder) => alder < 62))) && (
					<StyledAlert variant={'warning'} size={'small'}>
						Personer under 62 år har ikke rett på alderspensjon.
					</StyledAlert>
				)}
				{((nyBestilling && alderNyPerson > 61 && alderNyPerson < 67) ||
					(leggTil && alderLeggTilPerson > 61 && alderLeggTilPerson < 67) ||
					(importTestnorge && alderImportertPerson?.some((alder) => alder > 61 && alder < 67))) && (
					<StyledAlert variant={'info'} size={'small'}>
						Personer med alder fra 62 til 66 år vil kun få AP-vedtak om de har høy nok opptjening
						til å ta ut alderspensjon før 67 år. Opptjening kan legges inn i POPP.
					</StyledAlert>
				)}
				{!harNorskBankkonto && (
					// TODO: Sjekk norsk adresse også
					<StyledAlert variant={'warning'} size={'small'}>
						Personen må ha norsk bankkonto og adresse for at det skal fattes vedtak og for at
						vedtaksbrev skal kunne opprettes automatisk.
					</StyledAlert>
				)}

				<div className="flexbox--flex-wrap">
					<Monthpicker
						name={`${alderspensjonPath}.iverksettelsesdato`}
						label="Iverksettelsesmåned"
						date={_get(formikBag.values, `${alderspensjonPath}.iverksettelsesdato`)}
						handleDateChange={(dato: string) =>
							formikBag.setFieldValue(`${alderspensjonPath}.iverksettelsesdato`, dato)
						}
						minDate={setDate(addMonths(new Date(), 1), 1)}
					/>
					<FormikSelect
						name={`${alderspensjonPath}.uttaksgrad`}
						label="Uttaksgrad"
						options={Options('apUttaksgrad')}
						isClearable={false}
					/>
					<FormikSelect
						name={`${alderspensjonPath}.sivilstand`}
						label="Sivilstand"
						options={Options('apSivilstand')}
						size="large"
						isClearable={false}
					/>
					<FormikDatepicker
						name={`${alderspensjonPath}.sivilstatusDatoFom`}
						label="Sivilstand f.o.m. dato"
					/>
				</div>
			</Panel>
		</Vis>
	)
}

AlderspensjonForm.validation = validation
