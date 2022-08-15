package fr.jhelp.weatherservice.model.shared

class Temperature(val kelvin: Double) {
    val celsius = this.kelvin - 273.15
    val fahrenheit = (this.kelvin - 273.15) * 1.8 + 32.0

    override fun toString(): String = "${this.kelvin} K | ${this.celsius} C | ${this.fahrenheit} F"
}