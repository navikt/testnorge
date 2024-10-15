import React from 'react'
import { Alert } from '@navikt/ds-react'
import Loading from '@/components/ui/loading/Loading'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDateTime, showLabel, formatDate } from '@/utils/DataFormatter'
import { useYrkesskadeKodeverk } from '@/utils/hooks/useYrkesskade'

export const sjekkManglerYrkesskadeData = (yrkesskadeData) => {
	return !yrkesskadeData || yrkesskadeData?.length < 1
}

export const YrkesskaderVisning = ({ data, loading }) => {
	if (loading) {
		return <Loading label="Laster yrkesskade-data" />
	}

	if (!data) {
		return null
	}

	const manglerFagsystemData = sjekkManglerYrkesskadeData(data)

	const { kodeverkData, loading: yrkesskadeLoading, error } = useYrkesskadeKodeverk('ROLLETYPE')
	console.log('kodeverkData: ', kodeverkData) //TODO - SLETT MEG

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
						{(yrkesskade, idx) => {
							return (
								<React.Fragment key={idx}>
									<TitleValue title="Rolletype" value={yrkesskade?.data?.rolletype} />
									{/*//TODO: kodeverk*/}
									<TitleValue title="Innmelderrolle" value={yrkesskade?.data?.innmelderrolle} />
									{/*//TODO: kodeverk*/}
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
											{(periode, periodeIdx) => (
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
