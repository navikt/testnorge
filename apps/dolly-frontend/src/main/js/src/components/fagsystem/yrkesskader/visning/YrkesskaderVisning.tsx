import React from 'react'
import { Alert } from '@navikt/ds-react'
import Loading from '@/components/ui/loading/Loading'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDateTime, showLabel, formatDate } from '@/utils/DataFormatter'
import { useYrkesskadeKodeverk } from '@/utils/hooks/useYrkesskade'
import {
	YrkesskadePeriodeTypes,
	YrkesskaderTypes,
	YrkesskadeTypes,
} from '@/components/fagsystem/yrkesskader/YrkesskaderTypes'

type YrkesskaderVisningProps = {
	data: YrkesskaderTypes
	loading: boolean
}

export const sjekkManglerYrkesskadeData = (yrkesskadeData: any) => {
	return !yrkesskadeData || yrkesskadeData?.length < 1
}

const showKodeverkLabel = (kodeverkData: any, value: string) => {
	if (!kodeverkData) {
		return value
	}
	if (!value) {
		return null
	}
	return kodeverkData[value]?.verdi
}

export const YrkesskaderVisning = ({ data, loading }: YrkesskaderVisningProps) => {
	const { kodeverkData: kodeverkDataRolletype } = useYrkesskadeKodeverk('ROLLETYPE')
	const { kodeverkData: kodeverkDataInnmelderrolletype } =
		useYrkesskadeKodeverk('INNMELDERROLLETYPE')

	if (loading) {
		return <Loading label="Laster yrkesskade-data" />
	}

	if (!data) {
		return null
	}

	const manglerFagsystemData = sjekkManglerYrkesskadeData(data)

	return (
		<div>
			<SubOverskrift label="Yrkesskader" iconKind="sykdom" isWarning={manglerFagsystemData} />
			{manglerFagsystemData ? (
				<Alert variant="warning" size="small" inline style={{ marginBottom: '20px' }}>
					Fant ikke yrkesskade-data på person
				</Alert>
			) : (
				<div className="person-visning_content">
					<DollyFieldArray header={'Yrkesskade'} data={data}>
						{(yrkesskade: { data: YrkesskadeTypes }, idx: number) => {
							return (
								<React.Fragment key={idx}>
									<TitleValue
										title="Rolletype"
										value={showKodeverkLabel(kodeverkDataRolletype, yrkesskade?.data?.rolletype)}
									/>
									<TitleValue
										title="Innmelderrolle"
										value={showKodeverkLabel(
											kodeverkDataInnmelderrolletype,
											yrkesskade?.data?.innmelderrolle,
										)}
									/>
									<TitleValue
										title="Innmelder identifikator"
										value={yrkesskade?.data?.innmelderIdentifikator}
									/>
									<TitleValue title="På vegne av" value={yrkesskade?.data?.paaVegneAv} />
									<TitleValue
										title="Klassifisering"
										value={showLabel('klassifisering', yrkesskade?.data?.klassifisering)}
									/>
									<TitleValue title="Referanse" value={yrkesskade?.data?.referanse} />
									<TitleValue
										title="Ferdigstill sak"
										value={showLabel('ferdigstillSak', yrkesskade?.data?.ferdigstillSak)}
									/>
									<TitleValue
										title="Tidstype"
										value={showLabel('tidstype', yrkesskade?.data?.tidstype)}
									/>
									<TitleValue
										title="Skadetidspunkt"
										value={formatDateTime(yrkesskade?.data?.skadetidspunkt)}
									/>
									{yrkesskade?.data?.perioder?.length > 0 && (
										<DollyFieldArray header="Perioder" data={yrkesskade?.data?.perioder} nested>
											{(periode: YrkesskadePeriodeTypes, periodeIdx: number) => (
												<React.Fragment key={periodeIdx}>
													<TitleValue title="Fra dato" value={formatDate(periode?.fra)} />
													<TitleValue title="Til dato" value={formatDate(periode?.til)} />
												</React.Fragment>
											)}
										</DollyFieldArray>
									)}
								</React.Fragment>
							)
						}}
					</DollyFieldArray>
				</div>
			)}
		</div>
	)
}
