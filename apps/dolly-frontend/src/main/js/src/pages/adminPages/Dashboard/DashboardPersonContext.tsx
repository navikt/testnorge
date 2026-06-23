import React, { createContext, ReactNode, useContext, useEffect, useMemo, useState } from 'react'
import { format, isValid, parseISO, subDays, subYears } from 'date-fns'
import {
	asNumber,
	fillMissingPersonDays,
	toPersonTrendData,
	withinDateRange,
} from './dashboardUtils'
import { createPersonTrendChartOptions } from './dashboardChartOptions'
import type { DashboardBestillingerDTO } from '@/utils/hooks/useDashboard'

interface DashboardPersonContextType {
	// Date range
	selectedQuickRange: string | null
	fraDato: string
	tilDato: string
	todayDate: string
	onFraDatoChange: (value: string) => void
	onTilDatoChange: (value: string) => void
	onQuickRangeClick: (quickRangeValue: string) => void

	// Summary
	filteredPersonerLength: number
	summary: {
		bestillinger: number
		personerTotalt: number
		nye: number
		gjenopprettede: number
		navIdenter: number
		testnorgeIdenter: number
	}

	// Chart
	personTrendDataLength: number
	personTrendChartOptions: any
}

const DashboardPersonContext = createContext<DashboardPersonContextType | undefined>(undefined)

const QUICK_RANGE_OPTIONS = [
	{ value: 'week', label: 'Siste uke' },
	{ value: 'month', label: 'Siste måned' },
	{ value: 'threeMonths', label: 'Siste 3 måneder' },
	{ value: 'sixMonths', label: 'Siste 6 måneder' },
	{ value: 'year', label: 'Siste år' },
	{ value: 'all', label: 'All historikk' },
] as const

type QuickRangeValue = (typeof QUICK_RANGE_OPTIONS)[number]['value']

const isQuickRangeValue = (value: string): value is QuickRangeValue =>
	QUICK_RANGE_OPTIONS.some((option) => option.value === value)

interface DashboardPersonProviderProps {
	children: ReactNode
	dashboardBestillinger: DashboardBestillingerDTO[]
}

export const DashboardPersonProvider: React.FC<DashboardPersonProviderProps> = ({
	children,
	dashboardBestillinger,
}) => {
	const [selectedQuickRange, setSelectedQuickRange] = useState<string | null>(null)
	const [fraDato, setFraDato] = useState('')
	const [tilDato, setTilDato] = useState('')

	// Initialize date range once data is available
	useEffect(() => {
		if (!fraDato && !tilDato && dashboardBestillinger.length > 0) {
			const latestAvailableDateValue = dashboardBestillinger
				.map((personData) => personData.dato)
				.filter((dateValue): dateValue is string => Boolean(dateValue))
				.sort((a, b) => a.localeCompare(b))
				.at(-1)

			const latestAvailableDate = latestAvailableDateValue
				? parseISO(latestAvailableDateValue)
				: undefined

			const quickRangeAnchorDate =
				latestAvailableDate && isValid(latestAvailableDate) ? latestAvailableDate : new Date()

			const todayValue = format(new Date(), 'yyyy-MM-dd')
			const fromValue = format(subDays(quickRangeAnchorDate, 29), 'yyyy-MM-dd')

			setFraDato(fromValue)
			setTilDato(todayValue)
			setSelectedQuickRange('month')
		}
	}, [dashboardBestillinger, fraDato, tilDato])

	const latestAvailableDateValue = useMemo(
		() =>
			dashboardBestillinger
				.map((personData) => personData.dato)
				.filter((dateValue): dateValue is string => Boolean(dateValue))
				.sort((a, b) => a.localeCompare(b))
				.at(-1),
		[dashboardBestillinger],
	)

	const latestAvailableDate = useMemo(
		() => (latestAvailableDateValue ? parseISO(latestAvailableDateValue) : undefined),
		[latestAvailableDateValue],
	)

	const quickRangeAnchorDate = useMemo(
		() => (latestAvailableDate && isValid(latestAvailableDate) ? latestAvailableDate : new Date()),
		[latestAvailableDate],
	)

	const earliestAvailableDateValue = useMemo(
		() =>
			dashboardBestillinger
				.map((personData) => personData.dato)
				.filter((dateValue): dateValue is string => Boolean(dateValue))
				.sort((a, b) => a.localeCompare(b))
				.at(0),
		[dashboardBestillinger],
	)

	const todayDate = format(new Date(), 'yyyy-MM-dd')

	const applyQuickRange = (quickRangeValue: string) => {
		const toDateValue = format(quickRangeAnchorDate, 'yyyy-MM-dd')
		const fromDateValue =
			quickRangeValue === 'all'
				? (earliestAvailableDateValue ?? format(subYears(quickRangeAnchorDate, 1), 'yyyy-MM-dd'))
				: quickRangeValue === 'week'
					? format(subDays(quickRangeAnchorDate, 6), 'yyyy-MM-dd')
					: quickRangeValue === 'month'
						? format(subDays(quickRangeAnchorDate, 29), 'yyyy-MM-dd')
						: quickRangeValue === 'threeMonths'
							? format(subDays(quickRangeAnchorDate, 89), 'yyyy-MM-dd')
							: quickRangeValue === 'sixMonths'
								? format(subDays(quickRangeAnchorDate, 179), 'yyyy-MM-dd')
								: format(subYears(quickRangeAnchorDate, 1), 'yyyy-MM-dd')
		setFraDato(fromDateValue)
		setTilDato(toDateValue)
		if (isQuickRangeValue(quickRangeValue)) {
			setSelectedQuickRange(quickRangeValue)
		}
	}

	// Filter and aggregate person data
	const filteredPersoner = useMemo(
		() =>
			dashboardBestillinger
				.filter((personData) => withinDateRange(personData.dato, fraDato, tilDato))
				.sort((a, b) => a.dato.localeCompare(b.dato)),
		[dashboardBestillinger, fraDato, tilDato],
	)

	const completeFilteredPersoner = useMemo(
		() => fillMissingPersonDays(filteredPersoner, fraDato, tilDato),
		[filteredPersoner, fraDato, tilDato],
	)

	const summary = useMemo(
		() =>
			completeFilteredPersoner.reduce(
				(acc, personData) => {
					acc.bestillinger += asNumber(personData.bestillinger)
					acc.personerTotalt += asNumber(personData.personerTotalt)
					acc.nye += asNumber(personData.nye)
					acc.gjenopprettede += asNumber(personData.gjenopprettede)
					acc.navIdenter += asNumber(personData.navIdenter)
					acc.testnorgeIdenter += asNumber(personData.testnorgeIdenter)
					return acc
				},
				{
					bestillinger: 0,
					personerTotalt: 0,
					nye: 0,
					gjenopprettede: 0,
					navIdenter: 0,
					testnorgeIdenter: 0,
				},
			),
		[completeFilteredPersoner],
	)

	const personTrendData = useMemo(
		() => toPersonTrendData(completeFilteredPersoner),
		[completeFilteredPersoner],
	)

	const personTrendChartOptions = useMemo(
		() => createPersonTrendChartOptions(personTrendData, {}),
		[personTrendData],
	)

	const value: DashboardPersonContextType = {
		// Date range
		selectedQuickRange,
		fraDato,
		tilDato,
		todayDate,
		onFraDatoChange: (value: string) => {
			setFraDato(value)
			setSelectedQuickRange(null)
		},
		onTilDatoChange: (value: string) => {
			setTilDato(value)
			setSelectedQuickRange(null)
		},
		onQuickRangeClick: applyQuickRange,

		// Summary
		filteredPersonerLength: completeFilteredPersoner.length,
		summary,

		// Chart
		personTrendDataLength: personTrendData.length,
		personTrendChartOptions,
	}

	return <DashboardPersonContext.Provider value={value}>{children}</DashboardPersonContext.Provider>
}

export const useDashboardPerson = () => {
	const context = useContext(DashboardPersonContext)
	if (!context) {
		throw new Error('useDashboardPerson must be used within DashboardPersonProvider')
	}
	return context
}
