import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { arrayToString, showLabel } from '@/utils/DataFormatter'
import { Jobboensker } from '@/components/fagsystem/arbeidsplassen/ArbeidsplassenTypes'

type JobboenskerVisningProps = {
	data?: Jobboensker
}

export const JobboenskerVisning = ({ data }: JobboenskerVisningProps) => {
	if (
		!data ||
		((!data.occupations || data.occupations.length < 1) &&
			(!data.locations || data.locations.length < 1) &&
			(!data.workLoadTypes || data.workLoadTypes.length < 1) &&
			(!data.workScheduleTypes || data.workScheduleTypes.length < 1) &&
			(!data.occupationTypes || data.occupationTypes.length < 1) &&
			(!data.startOption || data.startOption.length < 1))
	) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<div className="flexbox--full-width">
					<h3>Jobbønsker</h3>
					<div className="flexbox--flex-wrap">
						<TitleValue
							title="Jobber og yrker"
							value={arrayToString(data.occupations?.map((jobb) => jobb?.title))}
						/>
						<TitleValue
							title="Områder"
							value={arrayToString(data.locations?.map((omraade) => omraade?.location))}
						/>
						<TitleValue
							title="Arbeidsmengde"
							value={arrayToString(
								data.workLoadTypes?.map((type) => showLabel('arbeidsmengde', type)),
							)}
						/>
						<TitleValue
							title="Arbeidstider"
							value={arrayToString(
								data.workScheduleTypes?.map((type) => showLabel('arbeidstid', type)),
							)}
						/>
						<TitleValue
							title="Ansettelsestyper"
							value={arrayToString(
								data.occupationTypes?.map((type) => showLabel('ansettelsestype', type)),
							)}
						/>
						<TitleValue title="Oppstart" value={showLabel('oppstart', data.startOption)} />
					</div>
				</div>
			</ErrorBoundary>
		</div>
	)
}
