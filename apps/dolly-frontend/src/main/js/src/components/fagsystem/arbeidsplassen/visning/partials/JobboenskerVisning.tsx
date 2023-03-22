import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import Formatters from '@/utils/DataFormatter'

export const JobboenskerVisning = ({ data }) => {
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
							value={Formatters.arrayToString(data.occupations?.map((jobb) => jobb?.title))}
						/>
						<TitleValue
							title="Områder"
							value={Formatters.arrayToString(data.locations?.map((omraade) => omraade?.location))}
						/>
						<TitleValue
							title="Arbeidsmengde"
							value={Formatters.arrayToString(
								data.workLoadTypes?.map((type) => Formatters.showLabel('arbeidsmengde', type))
							)}
						/>
						<TitleValue
							title="Arbeidstider"
							value={Formatters.arrayToString(
								data.workScheduleTypes?.map((type) => Formatters.showLabel('arbeidstid', type))
							)}
						/>
						<TitleValue
							title="Ansettelsestyper"
							value={Formatters.arrayToString(
								data.occupationTypes?.map((type) => Formatters.showLabel('ansettelsestype', type))
							)}
						/>
						<TitleValue
							title="Oppstart"
							value={Formatters.showLabel('oppstart', data.startOption)}
						/>
					</div>
				</div>
			</ErrorBoundary>
		</div>
	)
}
