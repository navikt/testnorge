import React from 'react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingData, BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { allCapsToCapitalized, formatDate, oversettBoolean, showLabel } from '@/utils/DataFormatter'
import _ from 'lodash'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'

const getTredjelandsborgerStatus = (oppholdKriterier: any, udiStubKriterier: any) => {
	if (oppholdKriterier && oppholdKriterier.oppholdSammeVilkaar) {
		return 'Oppholdstillatelse eller opphold på samme vilkår'
	} else if (oppholdKriterier && oppholdKriterier.uavklart) {
		return 'Uavklart'
	} else if (udiStubKriterier.harOppholdsTillatelse === false) {
		return 'Ikke oppholdstillatelse eller ikke opphold på samme vilkår'
	}
	return null
}

export const Udistub = ({ udistub }: any) => {
	if (!udistub || (isEmpty(udistub) && !udistub?.hasOwnProperty('flyktning'))) {
		return null
	}

	const oppholdStatus = udistub.oppholdStatus
	const arbeidsadgang = udistub.arbeidsadgang
	const aliaser = udistub.aliaser
	const flyktningstatus = udistub.flyktning
	const asylsoeker = udistub.soeknadOmBeskyttelseUnderBehandling

	const oppholdsrettTyper = [
		'eosEllerEFTABeslutningOmOppholdsrett',
		'eosEllerEFTAVedtakOmVarigOppholdsrett',
		'eosEllerEFTAOppholdstillatelse',
	]

	const currentOppholdsrettType =
		oppholdStatus && oppholdsrettTyper.find((type) => oppholdStatus[type])

	const currentTredjelandsborgereStatus = getTredjelandsborgerStatus(oppholdStatus, udistub)

	const oppholdsrett = Boolean(currentOppholdsrettType)
	const tredjelandsborger = Boolean(currentTredjelandsborgereStatus) ? 'Tredjelandsborger' : null

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>UDI</BestillingTitle>
				{oppholdStatus && (
					<>
						<h3>Gjeldende oppholdsstatus</h3>
						<BestillingData>
							<TitleValue
								title="Oppholdsstatus"
								value={oppholdsrett ? 'EØS-eller EFTA-opphold' : tredjelandsborger}
							/>
							<TitleValue
								title="Type opphold"
								value={
									oppholdsrett && showLabel('eosEllerEFTAtypeOpphold', currentOppholdsrettType)
								}
							/>
							<TitleValue title="Status" value={currentTredjelandsborgereStatus} />
							<TitleValue
								title="Oppholdstillatelse fra dato"
								value={formatDate(
									_.get(oppholdStatus, `${currentOppholdsrettType}Periode.fra`) ||
										_.get(oppholdStatus, 'oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.fra'),
								)}
							/>
							<TitleValue
								title="Oppholdstillatelse til dato"
								value={formatDate(
									_.get(oppholdStatus, `${currentOppholdsrettType}Periode.til`) ||
										_.get(oppholdStatus, 'oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.til'),
								)}
							/>
							<TitleValue
								title="Effektueringsdato"
								value={formatDate(
									_.get(oppholdStatus, `${currentOppholdsrettType}Effektuering`) ||
										_.get(oppholdStatus, 'oppholdSammeVilkaar.oppholdSammeVilkaarEffektuering'),
								)}
							/>
							<TitleValue
								title="Grunnlag for opphold"
								value={
									oppholdsrett &&
									showLabel(currentOppholdsrettType, oppholdStatus[currentOppholdsrettType])
								}
							/>
							<TitleValue
								title="Type oppholdstillatelse"
								value={showLabel(
									'oppholdstillatelseType',
									_.get(oppholdStatus, 'oppholdSammeVilkaar.oppholdstillatelseType'),
								)}
							/>
							<TitleValue
								title="Vedtaksdato"
								value={formatDate(
									_.get(oppholdStatus, 'oppholdSammeVilkaar.oppholdstillatelseVedtaksDato'),
								)}
							/>
							<TitleValue
								title="Avgjørelsesdato"
								value={formatDate(
									_.get(
										oppholdStatus,
										'ikkeOppholdstilatelseIkkeVilkaarIkkeVisum.avslagEllerBortfall.avgjorelsesDato',
									),
								)}
							/>
						</BestillingData>
					</>
				)}
				{arbeidsadgang && (
					<>
						<h3>Arbeidsadgang</h3>
						<BestillingData>
							<TitleValue
								title="Har arbeidsadgang"
								value={allCapsToCapitalized(arbeidsadgang.harArbeidsAdgang)}
							/>
							<TitleValue
								title="Type arbeidsadgang"
								value={showLabel('typeArbeidsadgang', arbeidsadgang.typeArbeidsadgang)}
							/>
							<TitleValue
								title="Arbeidsomfang"
								value={showLabel('arbeidsOmfang', arbeidsadgang.arbeidsOmfang)}
							/>
							<TitleValue
								title="Arbeidsadgang fra dato"
								value={formatDate(_.get(arbeidsadgang, 'periode.fra'))}
							/>
							<TitleValue
								title="Arbeidsadgang til dato"
								value={formatDate(_.get(arbeidsadgang, 'periode.til'))}
							/>
							<TitleValue title="Hjemmel" value={_.get(arbeidsadgang, 'hjemmel')} />
							<TitleValue title="Forklaring" value={_.get(arbeidsadgang, 'forklaring')} />
						</BestillingData>
					</>
				)}
				{aliaser?.length > 0 && (
					<DollyFieldArray header="Aliaser" data={aliaser} nested>
						{(alias: any, idx: number) => (
							<React.Fragment key={idx}>
								<TitleValue title="Type alias" value={alias.nyIdent ? 'ID-nummer' : 'Navn'} />
								<TitleValue title="Identtype" value={alias.identtype} />
							</React.Fragment>
						)}
					</DollyFieldArray>
				)}
				{(udistub?.hasOwnProperty('flyktning') || asylsoeker) && (
					<>
						<h3>Annet</h3>
						<BestillingData>
							<TitleValue title="Flyktningstatus" value={oversettBoolean(flyktningstatus)} />
							<TitleValue title="Asylsøker" value={allCapsToCapitalized(asylsoeker)} />
						</BestillingData>
					</>
				)}
			</ErrorBoundary>
		</div>
	)
}
